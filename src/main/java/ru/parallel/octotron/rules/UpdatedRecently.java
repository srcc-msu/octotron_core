/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EDependencyType;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.utils.JavaUtils;

public class UpdatedRecently extends OctoRule
{
	private static final long serialVersionUID = -5796823312858284235L;
	private final String measured_attribute;
	private long threshold = 0l;

	public UpdatedRecently(String name, String measured_attribute, long threshold)
	{
		super(name) ;
		this.measured_attribute = measured_attribute;
		this.threshold = threshold;
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

		return JavaUtils.GetTimestamp() - attr.GetATime() < threshold;
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
