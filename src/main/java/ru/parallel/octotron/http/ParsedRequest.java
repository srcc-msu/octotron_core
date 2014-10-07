/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
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
	private final ModelList path;

	public ParsedRequest(Operation operation, Map<String, String> params, ModelList path)
	{
		this.operation = operation;
		this.params = params;
		this.path = path;
	}

	public RequestResult Execute(ExecutionController exec_control)
	{
		try
		{
//			ModelList<? extends ModelEntity, ?> entity_list = null;

//			String path = params.get("path");

//			if(path != null)
//				entity_list = PathParser.Parse(path).Execute(exec_control);

			return (RequestResult) operation.Execute(exec_control, params, path);
		}
		catch(Exception e)
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
