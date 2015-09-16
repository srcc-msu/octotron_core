/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules.plain;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
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
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(param));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		Attribute attr = entity.GetAttribute(param);

		if(!attr.IsValid())
			return Value.invalid;

		return attr.ne(match_value);
	}

}
