/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

import java.util.Arrays;

public class StrictLogicalAnd extends Rule
{
	private final String[] attributes;

	public StrictLogicalAnd(String... attributes)
	{
		this.attributes = Arrays.copyOf(attributes, attributes.length);
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		for(String attr_name : attributes)
			result.add(entity.GetAttribute(attr_name));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		boolean res = true;

		for(String attr_name : attributes)
		{
			Attribute attr = entity.GetAttribute(attr_name);

			if(!attr.IsValid())
				return Value.invalid;

			res = res & attr.GetBoolean();
		}

		return res;
	}

}
