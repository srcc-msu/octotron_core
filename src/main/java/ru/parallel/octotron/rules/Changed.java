/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class Changed extends Rule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;

	public Changed(String name, String measured_attribute)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(measured_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(measured_attribute);

		if(!attr.IsValid() || attr.GetCTime() == 0)
			return GetDefaultValue();

		Object last_val = attr.GetLastValue();

		return !attr.GetValue().equals(last_val);
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
