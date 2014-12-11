package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.HTTPServer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class HttpService extends Service
{
	private final HTTPServer http;

	private final ThreadPoolExecutor http_executor;

	public HttpService(Context context, RequestService request_service)
			throws ExceptionSystemError
	{
		super(context);

		http_executor = new ThreadPoolExecutor(this.context.settings.GetNumThreads(), this.context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		http = new HTTPServer(context, http_executor, this, request_service);
	}

	@Override
	public void Finish()
	{
		// silently ignore all new requests
		http_executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		ShutdownExecutor(http_executor);

		http.Finish();
	}

	public void RequestInform()
	{
		context.stat.Add("http", 1, http_executor.getQueue().size());
	}
}
