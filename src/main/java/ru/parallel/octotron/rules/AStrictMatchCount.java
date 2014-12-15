/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class AStrictMatchCount extends AStrict
{
	private final Value value;

	public AStrictMatchCount(Object value, EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
		this.value = Value.Construct(value);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
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
