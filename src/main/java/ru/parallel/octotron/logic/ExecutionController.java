/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.model.*;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.http.HTTPServer;
import ru.parallel.octotron.http.ParsedHttpRequest;
import ru.parallel.octotron.http.RequestResult;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.netimport.SimpleImporter;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.DynamicSleeper;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO refactor this class
public class ExecutionController
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final GlobalSettings settings;
	private final IGraph graph;
	private final ModelService model_service = null;

	private ImportManager manager;
	private SimpleImporter http_importer;
	private SimpleImporter http_unchecked_importer;

	private HTTPServer http;

	private Thread request_processor;

	private ReactionInvoker rule_invoker;

	private boolean exit = false;
	private boolean silent = false;

	private final Queue<ParsedHttpRequest> request_queue
		= new ConcurrentLinkedQueue<>();

	private final Queue<ParsedHttpRequest> blocking_request_queue
		= new ConcurrentLinkedQueue<>();

	private Statistics stat;

	private SelfTest tester;

	public ExecutionController(IGraph graph, GlobalSettings settings)
		throws ExceptionSystemError
	{
		this.graph = graph;
		this.settings = settings;

		Init();
	}

	public void Init()
		throws ExceptionSystemError
	{
		manager = new ImportManager();

		http_importer = new SimpleImporter();
		http_unchecked_importer = new SimpleImporter();

		http = new HTTPServer(settings);

		BackgroundRequestProcess();

		rule_invoker = new ReactionInvoker(settings);

		stat = new Statistics();

		tester = new SelfTest(this);
	}

/**
 * start a separate thread for fetching and accumulating request messages
 * */
	private void BackgroundRequestProcess()
	{
		request_processor = new Thread()
		{
			@Override
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

				LOGGER.log(Level.INFO, "request processor thread finished");
			}
		};

		request_processor.setName("request_processor");
		request_processor.start();
	}

	public void Import(ModelEntity object, SimpleAttribute attribute)
	{
		http_importer.Put(object, attribute);
	}

	public void UncheckedImport(ModelEntity object, SimpleAttribute attribute)
	{
		http_unchecked_importer.Put(object, attribute);
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
		if(http != null)
			http.Finish();

		if(request_processor != null)
			request_processor.interrupt();

		if(rule_invoker != null)
			rule_invoker.Finish();

		if(manager != null)
			manager.Finish();
	}

	private final DynamicSleeper sleeper = new DynamicSleeper();

	public void Process(int max_count)
		throws IOException, InterruptedException, ExceptionSystemError
	{
		stat.Process();

		int processed_requests = ProcessRequests(request_queue, max_count);
		int processed_blocking_requests = ProcessRequests(blocking_request_queue, max_count);
		int processed_imports = ProcessImport(max_count);
		processed_imports += ProcessUncheckedImport(max_count);

		stat.Request(processed_requests, request_queue.size());
		stat.BlockingRequest(processed_blocking_requests, blocking_request_queue.size());
		stat.Import(processed_imports, http_importer.GetSize());

		stat.SleepTime(sleeper.GetSleepTime());
		sleeper.Sleep(processed_requests + processed_blocking_requests + processed_imports == 0);
	}

	AttributeList<ModelAttribute> ImmediateImport(ModelEntity entity, SimpleAttribute attribute)
	{
		List<Pair<ModelEntity, SimpleAttribute>> packet = new LinkedList<>();
		packet.add(Pair.of(entity, attribute));

		return manager.Process(packet);
	}

	/**
	 * process data import from all sources,<br>
	 * invoke reactions and sleep if needed<br>
	 * throws if encountered unknown import<br>
	 * */
	private int ProcessImport(int max_count)
	{
		List<Pair<ModelEntity, SimpleAttribute>> http_packet
			= http_importer.Get(max_count);
		int processed_http = http_packet.size();

		AttributeList<ModelAttribute> changed = manager.Process(http_packet);

		rule_invoker.Invoke(changed, silent);

		return processed_http;
	}

	/**
	 * process data import from all sources,<br>
	 * invoke reactions and sleep if needed<br>
	 * reports if encountered unknown import<br>
	 * */
	private int ProcessUncheckedImport(int max_count)
		throws ExceptionSystemError
	{
		List<Pair<ModelEntity, SimpleAttribute>> http_packet
			= http_unchecked_importer.Get(max_count);

		int processed_http = http_packet.size();

		for(Pair<ModelEntity, SimpleAttribute> pair : http_packet)
		{
			ModelEntity entity = pair.getLeft();
			SimpleAttribute attr = pair.getRight();

			if(!entity.TestAttribute(attr.GetName()))
			{
				entity.DeclareConstant(attr);
				String script = settings.GetScriptByKey("on_new_attribute");

				if(script != null)
				{
					FileUtils.ExecSilent(script
						, entity.GetAttribute("AID").GetLong().toString(), attr.GetName());
				}
			}
		}

		AttributeList<ModelAttribute> changed = manager.Process(http_packet);

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
			RequestResult res = parsed_request.GetParsedRequest().Execute(model_service, this);

			if(parsed_request.GetParsedRequest().IsBlocking())
				parsed_request.GetHttpRequest().Finish(res);

			if(res.type == E_RESULT_TYPE.ERROR)
			{
				LOGGER.log(Level.WARNING, "request failed "
					+ parsed_request.GetHttpRequest().GetPath()
					+ parsed_request.GetHttpRequest().GetQuery());

				LOGGER.log(Level.WARNING, res.data);
			}

			count ++;
		}

		return count;
	}

	private static final double free_space_kb_thr = 1024*1024; // 1GB in KB

	public String PerformSelfTest()
	{
		tester.Init();

		boolean graph_test = tester.Test();

		boolean process_test1 = request_processor.isAlive();
		boolean process_test2 = rule_invoker.IsAlive();

		long free_space_kb;
		String free_space_res;

		try
		{
			free_space_kb = FileSystemUtils.freeSpaceKb((new File("")).getAbsolutePath());
		}
		catch(IOException fail)
		{
			LOGGER.log(Level.WARNING, "could not get free disk space");
			free_space_kb = -1;
		}

		if(free_space_kb > 0)
			free_space_res = (free_space_kb > free_space_kb_thr) + " ( " + free_space_kb + "KB free )";
		else
			free_space_res = Boolean.valueOf(false).toString() + " ( can not get free space )";

		StringBuilder result = new StringBuilder();

		result.append("graph test: ").append(graph_test).append(System.lineSeparator());
		result.append("request processor: ").append(process_test1).append(System.lineSeparator());
		result.append("rule invoker: ").append(process_test2).append(System.lineSeparator());
		result.append("disk space: ").append(free_space_res).append(System.lineSeparator());

		return result.toString();
	}

/**
 * create snapshot of all reactions with failed conditions<br>
 * and get their description<br>
 * */
	public String MakeSnapshot() {
		StringBuilder result = new StringBuilder();

		ObjectList<ModelObject, ModelLink> list = ModelService.GetAllObjects();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		for(ModelObject obj : list)
		{
			for(OctoResponse response : obj.GetFails())
			{
				PreparedResponse prepared_response = new PreparedResponse(response, obj, JavaUtils.GetTimestamp());

				String descr = prepared_response.GetFullString();

				result.append(descr).append(System.lineSeparator());
			}
		}

		return result.toString();
	}

	public String GetStat()
	{
		return stat.GetStat();
	}

	// TODO: entities?
	public String CheckModTime(long interval)
	{
		StringBuilder result = new StringBuilder();

		ObjectList<ModelObject, ModelLink> list = ModelService.GetAllObjects();

		((Neo4jGraph)graph).GetTransaction().ForceWrite();

		long cur_time = JavaUtils.GetTimestamp();

		for(ModelObject obj : list)
		{
			for(ModelAttribute attr : obj.GetAttributes())
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
		InputStream stream = getClass().getResourceAsStream("/VERSION");

		if(stream == null)
			throw new ExceptionSystemError("missing VERSION file");

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		Map<String, String> version = new HashMap<>();

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
				LOGGER.log(Level.SEVERE, "failed to close the version file");
			}
		}

		return version;
	}
}
