/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class CalcSpeed extends Rule
{
	private final String measured_attribute;

	public CalcSpeed(String measured_attribute)
	{
		this.measured_attribute = measured_attribute;
	}

	@Override
	protected AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(measured_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(measured_attribute);

		if(!attr.GetValue().IsValid())
			return Value.invalid;

		return attr.GetSpeed();
	}
}
