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

public class UpperArgThreshold extends OctoRule
{
	private static final long serialVersionUID = 2191189080156471873L;
	private final String param;
	private final String arg_threshold;

	public UpperArgThreshold(String name, String param, String arg_threshold)
	{
		super(name) ;
		this.param = param;
		this.arg_threshold = arg_threshold;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));
		result.add(entity.GetAttribute(arg_threshold));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(param);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.lt(entity.GetAttribute(arg_threshold).GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
