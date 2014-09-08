/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class ToArgPct extends Rule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;
	private final String max_value_arg;

	public ToArgPct(String name, String measured_attribute, String max_value_arg)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
		this.max_value_arg = max_value_arg;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(measured_attribute));
		result.add(entity.GetMetaAttribute(max_value_arg));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(measured_attribute);
		ModelAttribute max_value = entity.GetAttribute(max_value_arg);

		if(!attr.IsValid() || attr.GetCTime() == 0)
			return GetDefaultValue();

		return (int)(attr.ToDouble() * 100.0 / max_value.ToDouble());
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
