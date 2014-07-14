/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class CalcSpeed extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;

	public CalcSpeed(String name, String measured_attribute)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
	}

	@Override
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(measured_attribute);

		if(!attr.IsValid() || attr.GetCTime() == 0)
			return GetDefaultValue();

		return attr.GetSpeed();
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0.0;
	}
}
