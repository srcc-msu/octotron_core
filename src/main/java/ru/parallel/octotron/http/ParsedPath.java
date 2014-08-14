/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.http.PathOperations.PathToken;
import ru.parallel.octotron.logic.ExecutionController;

import java.util.List;

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

	public EntityList<? extends ModelEntity, ?> Execute(ExecutionController exec_control)
		throws ExceptionParseError
	{
		EntityList<? extends ModelEntity, ?> entity_list = null;

		for(PathToken token : tokens)
		{
			entity_list = token.Transform(exec_control, entity_list);
		}

		return entity_list;
	}
}
