/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AggregateLongSum extends Aggregate
{
	public AggregateLongSum(String name, EDependencyType dependency, String... attributes)
	{
		super(name, dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		return (Long)res + attribute.GetLong();
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0L;
	}
}
