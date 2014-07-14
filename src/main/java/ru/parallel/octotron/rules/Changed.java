/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class Changed extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;

	public Changed(String name, String measured_attribute)
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

		Object last_val = attr.GetLastValue();

		return !attr.GetValue().equals(last_val);
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
