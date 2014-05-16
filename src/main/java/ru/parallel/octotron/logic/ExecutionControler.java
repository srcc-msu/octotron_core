/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.IGraph;
import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.core.OctoResponse;
import main.java.ru.parallel.octotron.exec.GlobalSettings;
import main.java.ru.parallel.octotron.http.HTTPServer;
import main.java.ru.parallel.octotron.http.ParsedHttpRequest;
import main.java.ru.parallel.octotron.http.RequestResult;
import main.java.ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.netimport.ISensorData;
import main.java.ru.parallel.octotron.netimport.SimpleImporter;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionImportFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.reactions.PreparedResponse;
import main.java.ru.parallel.octotron.utils.ObjectList;
import main.java.ru.parallel.utils.DynamicSleeper;
import main.java.ru.parallel.utils.JavaUtils;

public class ExecutionControler
{
	private boolean exit = false;

	private final IGraph graph;
	private final GraphService graph_service;
	private final ImportManager manager;

	private final SimpleImporter http_importer;
	private HTTPServer http;

	private Thread request_proccessor;

	private ReactionInvoker rule_invoker;
	private boolean silent = false;

	private final Queue<ParsedHttpRequest> request_queue
		= new ConcurrentLinkedQueue<ParsedHttpRequest>();

	private final Queue<ParsedHttpRequest> blocking_request_queue
		= new ConcurrentLinkedQueue<ParsedHttpRequest>();

	private Statistics stat;

	public ExecutionControler(IGraph graph, GraphService graph_service, GlobalSettings settings)
		throws ExceptionSystemError
	{
		this.graph = graph;
		this.graph_service = graph_service;
		this.manager = new ImportManager(graph_service);

		http_importer = new SimpleImporter();

		http = new HTTPServer(settings);

		BackgroundRequestProcess();

		this.rule_invoker = new ReactionInvoker(settings);

		this.stat = new Statistics();
	}

/**
 * start a separate thread for fetching and accumulating request messages
 * */
	private void BackgroundRequestProcess()
	{
		request_proccessor = new Thread()
		{
			public void run()
			{
				ParsedHttpRequest request;
				DynamicSleeper sleeper = new DynamicSleeper();

				try
				{
					while(true)
					{
						boolean added = false;

						if((request = http.GetRequest()) != null)
						{
							request_queue.add(request);

							added = true;
						}
						if((request = http.GetBlockingRequest()) != null)
						{
							blocking_request_queue.add(request);
							added = true;
						}

						sleeper.Sleep(!added);
					}
				}
				catch (InterruptedException ignore){}

				System.out.println("request processor thread finished");
			}
		};

		request_proccessor.setName("request_proccessor");
		request_proccessor.start();
	}

	public SimpleImporter GetImporter()
	{
		return http_importer;
	}

	public void SetExit(boolean exit)
	{
		this.exit = exit;
	}

	public void SetSilent(boolean silent)
	{
		this.silent = silent;
	}

	public boolean ShouldExit()
	{
		return exit;
	}

	public void Finish()
	{
		if(request_proccessor != null)
			request_proccessor.interrupt();

		rule_invoker.Finish();
		http.Finish();
		manager.Finish();
	}

	private final DynamicSleeper sleeper = new DynamicSleeper();

	public void Process(int max_count)
		throws IOException, ExceptionImportFail, InterruptedException
	{
		stat.Process();

		int processed_requests = ProcessRequests(request_queue, max_count);
		int processed_blocking_requests = ProcessRequests(blocking_request_queue, max_count);
		int processed_imports = ProcessImport(max_count);

		stat.Request(processed_requests, request_queue.size());
		stat.BlockingRequest(processed_blocking_requests, blocking_request_queue.size());
		stat.Import(processed_imports, http_importer.GetSize());

		stat.SleepTime(sleeper.GetSleepTime());
		sleeper.Sleep(processed_requests + processed_blocking_requests + processed_imports == 0);
	}

/**
 * execute all requests and process data import from all sources,<br>
 * invoke reactions and sleep if needed<br>
 * */
	private int ProcessImport(int max_count)
		throws ExceptionImportFail {
		List<? extends ISensorData> http_packet = http_importer.Get(max_count);
		int processed_http = http_packet.size();

		ObjectList changed = manager.Process(http_packet);

		rule_invoker.Invoke(changed, silent);

		return processed_http;
	}

	private int ProcessRequests(Queue<ParsedHttpRequest> queue, int max_count)
		throws IOException
	{
		if(queue.isEmpty())
			return 0;

		int count = 0;

		ParsedHttpRequest parsed_request;

		while((parsed_request = queue.poll()) != null && count < max_count)
		{
			RequestResult res = parsed_request.GetParsedRequest().Execute(graph_service, this);

			if(parsed_request.GetParsedRequest().IsBlocking())
				parsed_request.GetHttpRequest().Finish(res);

			if(res.type == E_RESULT_TYPE.ERROR)
			{
				System.err.println("request failed " + parsed_request.GetHttpRequest().GetQuery());
				System.err.println(res.data);
			}

			count ++;
		}

		return count;
	}

/**
 * create snapshot of all reactions with failed conditions<br>
 * and get their description<br>
 * */
	public String MakeSnapshot() {
		StringBuilder result = new StringBuilder();

		ObjectList list = graph_service.GetAllObjects();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		for(OctoObject obj : list)
		{
			for(OctoResponse response : obj.GetFails())
			{
				PreparedResponse preppared_response = new PreparedResponse(response, obj, JavaUtils.GetTimestamp());

				String descr = preppared_response.GetFullString();

				result.append(descr).append(System.lineSeparator());
			}
		}

		return result.toString();
	}

	public String GetStat()
	{
		return stat.GetStat();
	}

	public String CheckModTime(long interval)
	{
		StringBuilder result = new StringBuilder();

		ObjectList list = graph_service.GetAllObjects();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		long cur_time = JavaUtils.GetTimestamp();

		for(OctoObject obj : list)
		{
			for(OctoAttribute attr : obj.GetAttributes())
			{
				long diff = cur_time - attr.GetATime();

				if(diff > interval)
				{
					long aid = obj.GetAttribute("AID").GetLong();
					String type = obj.GetAttribute("type").GetString();

					result.append("[AID: ").append(aid)
						.append(", type: ").append(type)
						.append(", attribute: ").append(attr.GetName()).append("]: ")
						.append("last change: ").append(diff).append(" secs ago")
						.append(System.lineSeparator());
				}
			}
		}

		return result.toString();
	}

	public Map<String, String> GetVersion()
		throws ExceptionSystemError
	{
		InputStream stream = getClass().getResourceAsStream("/main/resources/VERSION");

		if(stream == null)
			throw new ExceptionSystemError("missing VERSION file");

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		Map<String, String> version = new HashMap<String, String>();

		try
		{
			String system = reader.readLine();
			String request = reader.readLine();
			String dsl = reader.readLine();

			if(system == null || request == null || dsl == null)
				throw new ExceptionSystemError("could not read version information");

			String[] system_arr = system.split("=");
			String[] request_arr = request.split("=");
			String[] dsl_arr = dsl.split("=");

			version.put(system_arr[0], system_arr[1]);
			version.put(request_arr[0], request_arr[1]);
			version.put(dsl_arr[0], dsl_arr[1]);
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				System.err.println("failed to close the version file");
			}
		}

		return version;
	}
}
