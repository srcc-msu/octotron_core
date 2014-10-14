/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AggregateNotMatchCount extends Aggregate
{
	private final Object value;

	public AggregateNotMatchCount(Object value, EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
		this.value = value;
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		if(attribute.ne(value))
			return (Long)res + 1;
		return res;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0L;
	}
}

