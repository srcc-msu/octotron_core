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

public class NotMatch extends Rule
{
	private final String param;
	private final Value match_value;

	public NotMatch(String param, Object match_value)
	{
		this.param = param;
		this.match_value = Value.Construct(match_value);
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

		return attr.ne(match_value);
	}

}
