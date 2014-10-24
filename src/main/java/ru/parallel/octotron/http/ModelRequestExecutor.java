/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.ExecutionController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO blocking and not blocking
public class ModelRequestExecutor implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final HttpExchangeWrapper exchange;
	private final ParsedModelRequest request;

	private final ExecutionController controller;

	public ModelRequestExecutor(ExecutionController controller
		, ParsedModelRequest request, HttpExchangeWrapper exchange)
	{
		this.controller = controller;
		this.request = request;
		this.exchange = exchange;
	}

	public ModelRequestExecutor(ExecutionController controller
		, ParsedModelRequest request)
	{
		this(controller, request, null);
	}

	public RequestResult GetResult()
	{
		try
		{
			ModelList<? extends ModelEntity, ?> entity_list = null;

			String path = request.params.get("path");

			if(path != null)
				entity_list = PathParser.Parse(path)
					.Execute(controller.GetContext().model_data);

			return (RequestResult) request.operation
				.Execute(controller, request.params, entity_list);
		}
		catch(Exception e)
		{
			String msg = "could not execute request: "
				+ request.GetQuery() + System.lineSeparator();

			msg += e + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				msg += System.lineSeparator() + s;

			if(exchange != null)
				LOGGER.log(Level.WARNING, "request failed: "
					+ exchange.GetPath()
					+ exchange.GetQuery());

			LOGGER.log(Level.WARNING, msg);

			return new RequestResult(RequestResult.E_RESULT_TYPE.ERROR, msg);
		}
	}

	@Override
	public void run()
	{
		RequestResult result = GetResult();

		if(exchange != null)
		{
			try
			{
				exchange.Finish(result);
			}
			catch (IOException error)
			{
				LOGGER.log(Level.WARNING, "could not finish request: ", error);
			}
		}
	}
}