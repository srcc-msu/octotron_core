package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.HTTPServer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class HttpService extends BGService
{
	private final HTTPServer http;

	public HttpService(String prefix, Context context, RequestService request_service)
			throws ExceptionSystemError
	{
		super(prefix, context, context.settings.GetNumThreads(), context.settings.GetNumThreads()
			, 0L, new LinkedBlockingQueue<Runnable>());

		http = new HTTPServer(context, executor, this, request_service);
	}

	@Override
	public void Finish()
	{
		// silently ignore all new requests
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		ShutdownExecutor(executor);

		http.Finish();
	}
}
