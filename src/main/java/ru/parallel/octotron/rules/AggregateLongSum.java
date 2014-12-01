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
	public AggregateLongSum(EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		if(!attribute.Check())
			return res;

		return (Long)res + attribute.GetLong();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0L;
	}
}
