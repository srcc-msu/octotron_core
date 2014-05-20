/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import java.util.Map;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.http.Operations.Operation;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.logic.ExecutionControler;
import ru.parallel.octotron.utils.AbsEntityList;

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

	public RequestResult Execute(GraphService graph_service, ExecutionControler exec_control)
	{
		try
		{
			AbsEntityList<?> entity_list = null;

			String path = params.get("path");

			if(path != null)
				entity_list = PathParser.Parse(path).Execute(graph_service, exec_control);

			return (RequestResult) operation.Execute(graph_service, exec_control, params, entity_list);
		}
		catch (Exception e)
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
