/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services.model;

import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.requests.ModelRequestExecutor;
import ru.parallel.octotron.http.requests.ParsedModelRequest;
import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.octotron.bg_services.BGService;

public class RequestService extends BGService
{
	public RequestService(Context context)
	{
		super(context, new BGExecutorWrapper("requests", context.settings.GetNumThreads()
			, DEFAULT_QUEUE_LIMIT));
	}

	public void AddRequest(ParsedModelRequest request)
	{
		executor.execute(new ModelRequestExecutor(request));
	}
}

