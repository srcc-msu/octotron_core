/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.graph.collections.IEntityList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
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

	public IEntityList<ModelEntity> Execute(ModelService model_service, ExecutionController exec_control)
		throws ExceptionParseError
	{
		IEntityList<ModelEntity> entity_list = null;

		for(PathToken token : tokens)
		{
			entity_list = token.Transform(model_service, exec_control, entity_list);
		}

		return entity_list;
	}
}
