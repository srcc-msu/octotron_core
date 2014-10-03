/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class Interval extends Rule
{
	private final String param;
	private final Object[] thresholds;

	public Interval(String name, String param, Object... thresholds)
	{
		super(name) ;
		this.param = param;
		this.thresholds = thresholds;
	}

	@Override
	public AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(param);

		if(!attr.IsValid())
			return null;

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
