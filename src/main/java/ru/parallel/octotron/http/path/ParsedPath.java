/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.path;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.services.ModelData;
import ru.parallel.octotron.http.path.PathOperations.PathToken;

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

	public ModelList<? extends ModelEntity, ?> Execute(ModelData model_data)
		throws ExceptionParseError
	{
		return Execute(null, model_data);
	}

	public ModelList<? extends ModelEntity, ?> Execute(ModelList<? extends ModelEntity, ?> entity_list, ModelData model_data)
		throws ExceptionParseError
	{
		ModelList<? extends ModelEntity, ?> result = entity_list;

		for(PathToken token : tokens)
		{
			result = token.Transform(model_data, result);
		}

		return result;
	}
}
