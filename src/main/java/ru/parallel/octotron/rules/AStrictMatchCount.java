/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.exception.ExceptionParseError;

public class AStrictMatchCount extends AStrict
{
	private final Value value;

	public AStrictMatchCount(Object value, String path, String... attributes)
		throws ExceptionParseError
	{
		super(path, attributes);
		this.value = Value.Construct(value);
	}

	@Override
	protected Object Accumulate(Object res, Attribute attribute)
	{
		if(attribute.eq(value))
			return (Long)res + 1;

		return res;
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
