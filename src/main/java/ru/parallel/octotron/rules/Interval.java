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

public class Interval extends Rule
{
	private static final long serialVersionUID = 2678310930245878138L;
	private final String param;
	private final Object[] thresholds;

	public Interval(String name, String param, Object... thresholds)
	{
		super(name) ;
		this.param = param;
		this.thresholds = thresholds;
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

		int result = 0;

		for(Object threshold : thresholds)
		{
			if(attr.ge(threshold))
				result++;
			else
				break;
		}

		return result;
	}

	@Override
	public Object GetDefaultValue()
	{
		return -1;
	}
}
