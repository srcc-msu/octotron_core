package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.HTTPServer;

import java.util.concurrent.LinkedBlockingQueue;

public class HttpService extends BGService
{
	private final HTTPServer http;

	public HttpService(String prefix, Context context, RequestService request_service)
			throws ExceptionSystemError
	{
		super(context
			, new BGExecutorService(prefix
				, context.settings.GetNumThreads(), context.settings.GetNumThreads()
				, 0L
				, new LinkedBlockingQueue<Runnable>(), 0L)); // unlimited - will keep all requests

		http = new HTTPServer(context, this, request_service);
	}

	@Override
	public void Finish()
	{
		super.Finish();
		http.Finish();
	}
}
