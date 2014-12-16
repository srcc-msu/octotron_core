/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class Interval extends Rule
{
	protected final String param;
	protected final Value[] thresholds;

	public Interval(String param, Object... thresholds)
	{
		this.param = param;

		this.thresholds = new Value[thresholds.length];

		for(int i = 0; i < thresholds.length; i++)
		{
			this.thresholds[i] = Value.Construct(thresholds[i]);
		}
	}

	@Override
	protected AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(param);

		if(!attr.GetValue().IsValid())
			return Value.invalid;

		long result = 0;

		for(Value threshold : thresholds)
		{
			if(attr.ge(threshold))
				result++;
			else
				break;
		}

		return result;
	}
}
