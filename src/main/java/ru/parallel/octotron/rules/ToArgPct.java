/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class ToArgPct extends Rule
{
	private final String measured_attribute;
	private final String max_value_arg;

	public ToArgPct(String measured_attribute, String max_value_arg)
	{
		this.measured_attribute = measured_attribute;
		this.max_value_arg = max_value_arg;
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(measured_attribute));
		result.add(entity.GetAttribute(max_value_arg));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		Attribute attr = entity.GetAttribute(measured_attribute);
		Attribute max_value = entity.GetAttribute(max_value_arg);

		if(!attr.IsValid() || !max_value.IsValid())
			return Value.invalid;

		return (long)(attr.ToDouble() * 100.0 / max_value.ToDouble());
	}
}
