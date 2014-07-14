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

public class LowerThreshold extends OctoRule
{
	private static final long serialVersionUID = 2678310930260346638L;
	private final String param;
	private final Object threshold;

	public LowerThreshold(String name, String param, Object threshold)
	{
		super(name) ;
		this.param = param;
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
		ModelAttribute attr = entity.GetAttribute(param);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.gt(threshold);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
