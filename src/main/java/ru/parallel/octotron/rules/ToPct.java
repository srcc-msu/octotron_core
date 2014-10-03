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

public class ToPct extends Rule
{
	private final String measured_attribute;
	private final int max_value;

	public ToPct(String name, String measured_attribute, int max_value)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
		this.max_value = max_value;
	}

	@Override
	public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(measured_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(measured_attribute);

		if(!attr.IsValid())
			return null;

		return (int)(attr.ToDouble() * 100.0 / (double) max_value);
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
