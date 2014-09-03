/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AggregateDoubleSum extends Aggregate
{
	private static final long serialVersionUID = -1961148475047706792L;

	public AggregateDoubleSum(String name, EDependencyType dependency, String... attributes)
	{
		super(name, dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IMetaAttribute attribute)
	{
		return (Double)res + attribute.GetDouble();
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0.0;
	}
}
