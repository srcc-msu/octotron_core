/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.http;

import java.util.List;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.http.PathOperations.PathToken;
import main.java.ru.parallel.octotron.logic.ExecutionControler;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionParseError;
import main.java.ru.parallel.octotron.utils.AbsEntityList;

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

	public AbsEntityList<?> Execute(GraphService graph_service, ExecutionControler exec_control)
		throws ExceptionParseError
	{
		AbsEntityList<?> entity_list = null;

		for(PathToken token : tokens)
		{
			entity_list = token.Transform(graph_service, exec_control, entity_list);
		}

		return entity_list;
	}
}
