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
	public AggregateDoubleSum(String name, EDependencyType dependency, String... attributes)
	{
		super(name, dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		return (Double)res + attribute.GetDouble();
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0.0;
	}
}
