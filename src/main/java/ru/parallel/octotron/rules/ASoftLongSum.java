/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class ASoftLongSum extends ASoft
{
	public ASoftLongSum(String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		if(!attribute.IsValid())
			return res;

		return (Long)res + attribute.GetLong();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
