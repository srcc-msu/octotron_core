/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AggregateMatchCount extends Aggregate
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final Object value;

	public AggregateMatchCount(String name, Object value, EDependencyType dependency, String... attributes)
	{
		super(name, dependency, attributes);
		this.value = value;
	}

	@Override
	protected Object Accumulate(Object res, ModelAttribute attribute)
	{
		if(attribute.eq(value))
			return (Long)res + 1;
		return res;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0L;
	}
}
