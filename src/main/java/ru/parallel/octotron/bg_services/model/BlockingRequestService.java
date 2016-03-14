package ru.parallel.octotron.bg_services.model;

import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.requests.HttpExchangeWrapper;
import ru.parallel.octotron.http.requests.ModelRequestExecutor;
import ru.parallel.octotron.http.requests.ParsedModelRequest;
import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.octotron.bg_services.BGService;

public class BlockingRequestService extends BGService
{
	public BlockingRequestService(Context context)
	{
		super(context, new BGExecutorWrapper("blocking_requests", context.settings.GetNumThreads()
			, DEFAULT_QUEUE_LIMIT));
	}

	public void AddRequest(ParsedModelRequest request, HttpExchangeWrapper http_exchange_wrapper)
	{
		executor.execute(new ModelRequestExecutor(request, http_exchange_wrapper));
	}
}
