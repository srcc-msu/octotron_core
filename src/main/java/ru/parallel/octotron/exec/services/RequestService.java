package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;
import ru.parallel.octotron.http.requests.ModelRequestExecutor;
import ru.parallel.octotron.http.requests.ParsedModelRequest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class RequestService extends Service
{
	/**
	 * processes all non-import operations
	 * */
	private ThreadPoolExecutor request_executor;
	private final ExecutionController execution_controller;

	public RequestService(Context context, ExecutionController execution_controller)
	{
		super(context);
		this.execution_controller = execution_controller;

		request_executor = new ThreadPoolExecutor(context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	}

	@Override
	public void Finish()
	{
		ShutdownExecutor(request_executor);
	}

	public void AddRequest(ParsedModelRequest request)
	{
		context.stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(execution_controller, request));
	}

	public void AddBlockingRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		context.stat.Add("request_executor", 1, request_executor.getQueue().size());

		request_executor.execute(new ModelRequestExecutor(execution_controller, request, http_exchange_wrapper));
	}
}
