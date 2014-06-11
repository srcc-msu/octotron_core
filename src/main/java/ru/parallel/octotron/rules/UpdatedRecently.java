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

		return JavaUtils.GetTimestamp() - attr.GetATime() < threshold;
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
