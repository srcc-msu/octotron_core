/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.http.HTTPServer;
import ru.parallel.octotron.http.HttpExchangeWrapper;
import ru.parallel.octotron.http.ModelRequestExecutor;
import ru.parallel.octotron.http.ParsedModelRequest;
import ru.parallel.octotron.logic.Importer;
import ru.parallel.octotron.logic.Statistics;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class ExecutionController
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Context context;

	private HTTPServer http;

	/**
	 * serves http requests
	 * */
	private ThreadPoolExecutor http_executor;

	/**
	 * processes all non-import operations
	 * */
	private ThreadPoolExecutor request_executor;

	/**
	 * single threaded pool
	 * it processes sensors import, varyings modification and, reactions processing
	 * the only pool, that modifies the model
	 * */
	private ThreadPoolExecutor import_executor;

	/**
	 * processes all reactions
	 * */
	private ThreadPoolExecutor reactions_executor;

	private boolean exit = false;
	private boolean silent = false;

	private Statistics stat;

	public List<java.util.Map<String, Object>> GetStat()
	{
		return stat.GetStat();
	}

	public ExecutionController(Context context)
		throws ExceptionSystemError
	{
		this.context = context;

		Init();
	}

	public void Init()
		throws ExceptionSystemError
	{
		//Executors
		import_executor = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		reactions_executor = new ThreadPoolExecutor(context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		request_executor = new ThreadPoolExecutor(context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		http_executor = new ThreadPoolExecutor(context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		http = new HTTPServer(this, http_executor);

		stat = new Statistics();
	}

	public void Import(ModelEntity entity, SimpleAttribute attribute)
	{
		import_executor.execute(new Importer(this, entity, attribute));
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

	public void Process()
		throws InterruptedException
	{
		Thread.sleep(1);
		stat.Process();
	}

	public void AddRequest(ParsedModelRequest request)
	{
		stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(this, request));
	}

	public void AddBlockingRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(this, request, http_exchange_wrapper));
	}

	public void Finish()
	{
		LOGGER.log(Level.INFO, "waiting for all tasks to finish");

		// silently ignore all new requests
		http_executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		ShutdownExecutor(http_executor);
		ShutdownExecutor(request_executor);
		ShutdownExecutor(import_executor);
		ShutdownExecutor(reactions_executor);

		http.Finish();

		FileUtils.Finish();

		LOGGER.log(Level.INFO, "all processing finished");
	}

	public void CheckReactions(AttributeList<IModelAttribute> attributes)
	{
		long time = JavaUtils.GetTimestamp();

		for(IModelAttribute attribute : attributes)
		{
			for(Response response : attribute.ProcessReactions())
			{
				AddResponse(new PreparedResponse(response
					, attribute.GetParent()
					, time
					, context.settings));
			}
		}
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

	public void HttpRequestInform()
	{
		stat.Add("http", 1, http_executor.getQueue().size());
	}

	public Context GetContext()
	{
		return context;
	}

	public void UnknownImport(ModelEntity target, SimpleAttribute attribute)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKey("on_new_attribute");

		try
		{
			FileUtils.ExecSilent(script
				, target.GetAttribute("AID").GetStringValue(), attribute.GetName());
		}
		catch (ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not execute script: " + script, e);
			throw e;
		}
	}
}
