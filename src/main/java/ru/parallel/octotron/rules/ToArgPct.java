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

public class ToArgPct extends Rule
{
	private final String measured_attribute;
	private final String max_value_arg;

	public ToArgPct(String name, String measured_attribute, String max_value_arg)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
		this.max_value_arg = max_value_arg;
	}

	@Override
	public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(measured_attribute));
		result.add(entity.GetAttribute(max_value_arg));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(measured_attribute);
		IModelAttribute max_value = entity.GetAttribute(max_value_arg);

		if(!attr.IsValid())
			return null;

		return (int)(attr.ToDouble() * 100.0 / max_value.ToDouble());
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
