/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class AValidCount extends ASoft
{
	public AValidCount(String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		if(attribute.IsValid())
			return (Long)res + 1;

		return res;
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
