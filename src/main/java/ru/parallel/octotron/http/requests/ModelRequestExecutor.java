/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.requests;

import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TypedString;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelRequestExecutor implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final HttpExchangeWrapper exchange;
	private final ParsedModelRequest request;

	public ModelRequestExecutor(ParsedModelRequest request, HttpExchangeWrapper exchange)
	{
		this.request = request;
		this.exchange = exchange;
	}

	public ModelRequestExecutor(ParsedModelRequest request)
	{
		this(request, null);
	}

	public TypedString GetResult()
	{
		try
		{
			return request.operation.Execute(request.params);
		}
		catch(Exception e)
		{
			String msg = "could not execute request: "
				+ request.operation.GetName() + "?" + request.GetQuery() + System.lineSeparator();

			msg += e.getMessage() + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				msg += System.lineSeparator() + s;

			if(exchange != null)
				LOGGER.log(Level.WARNING, "request failed: "
					+ exchange.GetPath()
					+ exchange.GetQuery());

			LOGGER.log(Level.WARNING, msg);

			return new ErrorString(e.getMessage());
		}
	}

	@Override
	public void run()
	{
		TypedString result = GetResult();

		if(exchange != null)
		{
			try
			{
				exchange.Finish(result);
			}
			catch(IOException error)
			{
				LOGGER.log(Level.WARNING, "could not finish request: ", error);
			}
		}
	}
}