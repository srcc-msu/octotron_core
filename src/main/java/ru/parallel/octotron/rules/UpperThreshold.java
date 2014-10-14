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

public class UpperThreshold extends Rule
{
	private final String param;
	private final Object threshold;

	public UpperThreshold(String param, Object threshold)
	{
		this.param = param;
		this.threshold = threshold;
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

		return attr.lt(threshold);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
