/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

import java.util.Arrays;

public class LogicalAnd extends Rule
{
	private final String[] attributes;

	public LogicalAnd(String... attributes)
	{
		this.attributes = Arrays.copyOf(attributes, attributes.length);
	}

	@Override
	public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

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
			IModelAttribute attr = entity.GetAttribute(attr_name);

			res = res & attr.GetBoolean();
		}

		return res;
	}

}
