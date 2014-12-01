/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AggregateDoubleSum extends Aggregate
{
	public AggregateDoubleSum(EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		if(!attribute.Check())
			return res;

		return (Double)res + attribute.GetDouble();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0.0;
	}
}
