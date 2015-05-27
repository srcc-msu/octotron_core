package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;
import ru.parallel.octotron.http.requests.ModelRequestExecutor;
import ru.parallel.octotron.http.requests.ParsedModelRequest;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;

public class RequestService extends BGService
{
	/**
	 * processes all non-import operations
	 * */

	public RequestService(Context context)
	{
		super(context, new BGExecutorService("requests", context.settings.GetNumThreads()
			, DEFAULT_QUEUE_LIMIT));
	}

	public void AddRequest(ParsedModelRequest request)
	{
		executor.execute(new ModelRequestExecutor(request));
	}

	public void AddBlockingRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		executor.execute(new ModelRequestExecutor(request, http_exchange_wrapper));
	}
}
