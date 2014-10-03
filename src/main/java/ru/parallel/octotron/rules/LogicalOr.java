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

public class LogicalOr extends Rule
{
	private final String[] attributes;

	public LogicalOr(String name, String... attributes)
	{
		super(name);
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
		boolean res = false;

		for(String attr_name : attributes)
		{
			IModelAttribute attr = entity.GetAttribute(attr_name);

			if(!attr.IsValid())
				return null;

			res = res | attr.GetBoolean();
		}

		return res;
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
