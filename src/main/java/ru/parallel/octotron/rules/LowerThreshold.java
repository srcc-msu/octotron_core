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

public class LowerThreshold extends Rule
{
	private final String param;
	private final Value threshold;

	public LowerThreshold(String param, Object threshold)
	{
		this.param = param;
		this.threshold = Value.Construct(threshold);
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

		return attr.gt(threshold);
	}

}
