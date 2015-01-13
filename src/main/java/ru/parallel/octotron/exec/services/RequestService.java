package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;
import ru.parallel.octotron.http.requests.ModelRequestExecutor;
import ru.parallel.octotron.http.requests.ParsedModelRequest;

import java.util.concurrent.LinkedBlockingQueue;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class RequestService extends BGService
{
	/**
	 * processes all non-import operations
	 * */
	private final ExecutionController execution_controller;

	public RequestService(String prefix, Context context, ExecutionController execution_controller)
	{
		super(prefix, context, context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, new LinkedBlockingQueue<Runnable>());
		this.execution_controller = execution_controller;
	}

	@Override
	public void Finish()
	{
		ShutdownExecutor(executor);
	}

	public void AddRequest(ParsedModelRequest request)
	{
		executor.execute(new ModelRequestExecutor(execution_controller, request));
	}

	public void AddBlockingRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		executor.execute(new ModelRequestExecutor(execution_controller, request, http_exchange_wrapper));
	}
}
