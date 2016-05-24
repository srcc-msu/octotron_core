/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.exception.ExceptionParseError;

public class ObjectCount extends ASoft
{
	public ObjectCount(String path)
			throws ExceptionParseError
	{
		super(path, "_id");
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		return res + 1;
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
