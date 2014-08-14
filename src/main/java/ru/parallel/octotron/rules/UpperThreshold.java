/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.OctoRule;

public class UpperThreshold extends OctoRule
{
	private static final long serialVersionUID = 2191189080156471873L;
	private final String param;
	private final Object threshold;

	public UpperThreshold(String name, String param, Object threshold)
	{
		super(name) ;
		this.param = param;
		this.threshold = threshold;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(param);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.lt(threshold);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
