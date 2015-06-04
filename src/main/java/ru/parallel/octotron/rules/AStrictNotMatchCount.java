/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public class AStrictNotMatchCount extends AStrict
{
	private final Value value;

	public AStrictNotMatchCount(Object value, String path, String... attributes)
		throws ExceptionParseError
	{		super(path, attributes);
		this.value = Value.Construct(value);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		if(attribute.ne(value))
			return (Long)res + 1;

		return res;
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}

