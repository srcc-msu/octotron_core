package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;

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

	@Override
	public void run()
	{
		RequestResult result;
		try
		{
			ModelList<? extends ModelEntity, ?> entity_list = null;

			String path = request.params.get("path");

			if(path != null)
				entity_list = PathParser.Parse(path).Execute();

			result = (RequestResult) request.operation.Execute(request.params, entity_list);

			if(exchange != null)
				exchange.Finish(result);
		}
		catch(Exception e)
		{
			String msg = "could not execute request: " + e + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				msg += System.lineSeparator() + s;

			LOGGER.log(Level.WARNING, "request failed: "
				+ exchange.GetPath()
				+ exchange.GetQuery());

			LOGGER.log(Level.WARNING, msg);

			if(exchange != null)
			{
				try
				{
					exchange.Finish(new RequestResult(RequestResult.E_RESULT_TYPE.ERROR, msg));
				}
				catch (IOException error)
				{
					LOGGER.log(Level.WARNING, "could not finish request: ", error);
				}
			}
		}
	}
}