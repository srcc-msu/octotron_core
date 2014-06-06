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

public class Changed extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;

	public Changed(String attribute_name, String measured_attribute)
	{
		super(attribute_name);
		this.measured_attribute = measured_attribute;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(measured_attribute);

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
