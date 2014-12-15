/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class ASoftDoubleSum extends ASoft
{
	public ASoftDoubleSum(EDependencyType dependency, String... attributes)
	{
		super(dependency, attributes);
	}

	@Override
	protected Object Accumulate(Object res, IModelAttribute attribute)
	{
		return (Double)res + attribute.GetDouble();
	}

	@Override
	protected Object GetDefaultValue()
	{
		return 0.0;
	}
}
