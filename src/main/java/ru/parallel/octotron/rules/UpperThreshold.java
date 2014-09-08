/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class UpperThreshold extends Rule
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
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(param);

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
