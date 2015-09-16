/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class ASoftDoubleSum extends ASoft
{
	public ASoftDoubleSum(String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		if(!attribute.IsValid())
			return res;

		return (Double)res + attribute.GetDouble();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0.0;
	}
}
