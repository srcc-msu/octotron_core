/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AStrictLongSum extends AStrict
{
	public AStrictLongSum(EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
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
