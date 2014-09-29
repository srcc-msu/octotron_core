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

public class UpperArgThreshold extends Rule
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
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(param));
		result.add(entity.GetMetaAttribute(arg_threshold));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(param);

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
