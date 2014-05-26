/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class CalcSpeed extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attr;

	public CalcSpeed(String speed_attr, String measured_attr)
	{
		super(speed_attr);
		this.measured_attr = measured_attr;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(measured_attr);

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
