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

public class ToPct extends Rule
{
	private final String measured_attribute;
	private final double max_value;

	public ToPct(String measured_attribute, double max_value)
	{
		this.measured_attribute = measured_attribute;
		this.max_value = max_value;
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(measured_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		Attribute attr = entity.GetAttribute(measured_attribute);

		if(!attr.GetValue().IsValid())
			return Value.invalid;

		return (long)(attr.ToDouble() * 100.0 / max_value);
	}
}
