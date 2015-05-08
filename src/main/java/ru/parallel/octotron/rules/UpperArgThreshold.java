/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class UpperArgThreshold extends Rule
{
	private final String param;
	private final String arg_threshold;

	public UpperArgThreshold(String param, String arg_threshold)
	{
		this.param = param;
		this.arg_threshold = arg_threshold;
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		Attribute attr = entity.GetAttribute(param);
		Attribute cmp = entity.GetAttribute(arg_threshold);

		if(!attr.GetValue().IsValid() || !cmp.GetValue().IsValid())
			return Value.invalid;

		return attr.lt(cmp.GetValue());
	}

}
