/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules.plain;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class Manual extends Rule
{
	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		return new AttributeList<>();
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		return false;
	}
}
