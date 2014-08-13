/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.IEntityList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.http.Operations.Operation;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.logic.ExecutionController;

import java.util.Map;

/**
 * parsed request with correct tokens list<br>
 * */
public class ParsedRequest
{
	private final Operation operation;
	private final Map<String, String> params;

	public ParsedRequest(Operation operation, Map<String, String> params)
	{
		this.operation = operation;
		this.params = params;
	}

	public RequestResult Execute(ModelService model_service, ExecutionController exec_control)
	{
		try
		{
			IEntityList<? extends ModelEntity> entity_list = null;

			String path = params.get("path");

			if(path != null)
				entity_list = PathParser.Parse(path).Execute(model_service, exec_control);

			return (RequestResult) operation.Execute(model_service, exec_control, params, entity_list);
		}
		catch(ExceptionParseError e)
		{
			String res = "could not execute request: " + e + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				res += System.lineSeparator() + s;

			return new RequestResult(E_RESULT_TYPE.ERROR, res);
		}
	}

	public boolean IsBlocking()
	{
		return operation.IsBlocking();
	}
}
