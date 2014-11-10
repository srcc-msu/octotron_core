/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.Utils;
import ru.parallel.octotron.http.path.PathParser;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public abstract class ModelOperation extends FormattedOperation
{
	public ModelOperation(String name, boolean is_blocking)
	{
		super(name, is_blocking);
	}

	@Override
	public final TypedString Execute(ExecutionController controller
		, Map<String, String> params, boolean verbose) throws ExceptionParseError
	{
		Utils.RequiredParams(params, "path");

		String path = params.get("path");
		params.remove("path");

		ModelList<? extends ModelEntity, ?> entities = PathParser.Parse(path)
			.Execute(controller.GetContext().model_data);

		if(entities.size() == 0)
			return new ErrorString("no entities found on path: " + path);

		return Execute(controller, params, verbose, entities);
	}

	public abstract TypedString Execute(ExecutionController controller
		, Map<String, String> params
		, boolean verbose
		, ModelList<? extends ModelEntity, ?> entities)
		throws ExceptionParseError;

}