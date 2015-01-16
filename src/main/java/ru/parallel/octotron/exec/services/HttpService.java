package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.HTTPServer;

public class HttpService extends BGService
{
	private final HTTPServer http;

	public HttpService(String prefix, Context context, RequestService request_service)
			throws ExceptionSystemError
	{
		// unlimited - will keep all requests
		super(context, new BGExecutorService(prefix, context.settings.GetNumThreads(), 0L));

		http = new HTTPServer(context, this, request_service);
	}

	@Override
	public void Finish()
	{
		super.Finish();
		http.Finish();
	}
}
