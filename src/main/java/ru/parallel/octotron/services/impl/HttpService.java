/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.HTTPServer;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;

public class HttpService extends BGService
{
	private final HTTPServer http;

	public HttpService(Context context)
			throws ExceptionSystemError
	{
		// unlimited - will keep all requests
		super(context, new BGExecutorService("http_requests", context.settings.GetNumThreads(), 0L));

		http = new HTTPServer(context, this);
	}

	@Override
	public void Finish()
	{
		super.Finish();
		http.Finish();
	}
}
