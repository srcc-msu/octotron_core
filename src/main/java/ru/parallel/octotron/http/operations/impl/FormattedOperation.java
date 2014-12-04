/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.IOperation;
import ru.parallel.utils.JsonUtils;
import ru.parallel.utils.format.JsonString;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

import static ru.parallel.utils.AutoFormat.ToJsonp;

public abstract class FormattedOperation implements IOperation
{
	private final String name;
	private final boolean is_blocking;

	public FormattedOperation(String name, boolean is_blocking)
	{
		this.name = name;
		this.is_blocking = is_blocking;
	}

	public String GetName()
	{
		return name;
	}

	public boolean IsBlocking()
	{
		return is_blocking;
	}

	@Override
	public final TypedString Execute(ExecutionController controller, Map<String, String> params)
		throws ExceptionParseError
	{
		String callback = params.get("callback");
		params.remove("callback");

		boolean verbose = false;

		if(params.containsKey("v"))
		{
			params.remove("v");
			verbose = true;
		}

		TypedString result = Execute(controller, params, verbose);

		if(callback != null)
			return ToJsonp(result, callback);
		else if(result instanceof JsonString)
			return JsonUtils.Prettify((JsonString) result);

		return result;
	}

	/**
	 * entity is not empty
	 * */
	protected abstract TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
		throws ExceptionParseError;
}