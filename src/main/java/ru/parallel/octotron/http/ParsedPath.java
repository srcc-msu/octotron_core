/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import java.util.List;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.http.PathOperations.PathToken;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.IEntityList;

/**
 * parsed request with correct tokens list<br>
 * */
public class ParsedPath
{
	private final List<PathToken> tokens;

	public ParsedPath(List<PathToken> tokens)
	{
		this.tokens = tokens;
	}

	public IEntityList<?> Execute(GraphService graph_service, ExecutionController exec_control)
		throws ExceptionParseError
	{
		IEntityList<?> entity_list = null;

		for(PathToken token : tokens)
		{
			entity_list = token.Transform(graph_service, exec_control, entity_list);
		}

		return entity_list;
	}
}
