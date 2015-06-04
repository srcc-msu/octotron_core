/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class AStrictDoubleSum extends AStrict
{
	public AStrictDoubleSum(String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		return (Double)res + attribute.GetDouble();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0.0;
	}
}
