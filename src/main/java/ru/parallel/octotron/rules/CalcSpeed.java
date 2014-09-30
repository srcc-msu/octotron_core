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

public class CalcSpeed extends Rule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;

	public CalcSpeed(String name, String measured_attribute)
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

		if(!attr.IsValid())
			return null;

		return attr.GetSpeed();
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0.0;
	}
}
