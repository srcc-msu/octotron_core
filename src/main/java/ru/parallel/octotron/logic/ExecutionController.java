/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.http.HTTPServer;
import ru.parallel.octotron.http.HttpExchangeWrapper;
import ru.parallel.octotron.http.ModelRequestExecutor;
import ru.parallel.octotron.http.ParsedModelRequest;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.FileUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class ExecutionController
{
	private static ExecutionController INSTANCE = null;

	public static void Init(GlobalSettings settings)
		throws ExceptionSystemError
	{
		ExecutionController.INSTANCE = new ExecutionController(settings);
	}

	public static ExecutionController Get()
	{
		return INSTANCE;
	}

	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final GlobalSettings settings;

	private HTTPServer http;

	private ThreadPoolExecutor import_executor;
	private ThreadPoolExecutor request_executor;
	private ThreadPoolExecutor http_executor;
	private ThreadPoolExecutor reactions_executor;
	private ThreadPoolExecutor reactions_invoker;

	private boolean exit = false;
	private boolean silent = false;

	private Statistics stat;

	public List<java.util.Map<String, Object>> GetStat()
	{
		return stat.GetStat();
	}

	private ExecutionController(GlobalSettings settings)
		throws ExceptionSystemError
	{
		this.settings = settings;

		Init();
	}

	public void Init()
		throws ExceptionSystemError
	{
		//Executors
		import_executor = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		reactions_executor = new ThreadPoolExecutor(settings.GetNumThreads(), settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		reactions_invoker = new ThreadPoolExecutor(settings.GetNumThreads(), settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		request_executor = new ThreadPoolExecutor(settings.GetNumThreads(), settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		http_executor = new ThreadPoolExecutor(settings.GetNumThreads(), settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		http = new HTTPServer(settings, http_executor);

		stat = new Statistics();
	}

	public void Import(ModelObject object, SimpleAttribute attribute)
	{
		import_executor.execute(new Importer(object, attribute));
		stat.Add("import_executor", 1, import_executor.getQueue().size());
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

	/**
	 * process data import from all sources,<br>
	 * invoke reactions and sleep if needed<br>
	 * reports if encountered unknown import<br>
	 * */
/*	private int ProcessUncheckedImport(int max_count)
		throws ExceptionSystemError
	{
		List<ImportManager.Packet> http_packet = http_unchecked_importer.Get(max_count);

		int processed_http = http_packet.size();

		for(ImportManager.Packet packet : http_packet)
		{
			if(!packet.object.TestAttribute(packet.attribute.GetName()))
			{
				packet.object.GetBuilder().DeclareConst(packet.attribute);
				String script = settings.GetScriptByKey("on_new_attribute");

				if(script != null)
				{
					FileUtils.ExecSilent(script
						, packet.object.GetAttribute("AID").GetLong().toString(), packet.attribute.GetName());
				}
			}
		}

		AttributeList<IModelAttribute> changed = manager.Process(http_packet);

//		rule_invoker.Invoke(changed, silent);

		return processed_http;
	}*/

	public void Process()
		throws InterruptedException
	{
		Thread.sleep(1);
		stat.Process();
	}

	public void AddRequest(ParsedModelRequest request)
	{
		stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(request));
	}

	public void AddBlockingRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(request, http_exchange_wrapper));
	}

	public void UncheckedImport(ModelObject target, SimpleAttribute attribute)
	{

	}

	public void Finish()
	{
		LOGGER.log(Level.INFO, "waiting for all tasks to finish");

		ShutdownExecutor(request_executor);
		ShutdownExecutor(import_executor);
		ShutdownExecutor(reactions_executor);
		ShutdownExecutor(reactions_invoker);
		ShutdownExecutor(http_executor);

		// http server throws something if closed too fast..
		try { Thread.sleep(10); } catch (InterruptedException ignore){}

		http.Finish();

		FileUtils.Finish();

		LOGGER.log(Level.INFO, "all processing finished");

		ExecutionController.INSTANCE = null;
	}

	public void CheckReactions(AttributeList<IModelAttribute> attributes)
	{
		stat.Add("reactions_invoker", 1, reactions_invoker.getQueue().size());

		reactions_invoker.execute(new ReactionInvoker(attributes));
	}

	public GlobalSettings GetSettings()
	{
		return settings;
	}

	public void AddResponse(PreparedResponse response)
	{
		stat.Add("reactions_executor", 1, reactions_executor.getQueue().size());

		reactions_executor.execute(response);
	}

	public boolean IsSilent()
	{
		return silent;
	}

	public void NewHttp()
	{
		stat.Add("http", 1, http_executor.getQueue().size());
	}
}
