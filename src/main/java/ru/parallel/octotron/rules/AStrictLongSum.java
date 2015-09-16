/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class AStrictLongSum extends AStrict
{
	public AStrictLongSum(String path, String... attributes)
		throws ExceptionParseError
	{		super(path, attributes);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		return (Long)res + attribute.GetLong();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
