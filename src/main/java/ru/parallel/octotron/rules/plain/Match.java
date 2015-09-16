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

public class Match extends Rule
{
	private final String check_attribute;
	private final Value match_value;

	public Match(String check_attribute, Object match_value)
	{
		this.check_attribute = check_attribute;
		this.match_value = Value.Construct(match_value);
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		Attribute attr = entity.GetAttribute(check_attribute);

		if(!attr.IsValid())
			return Value.invalid;

		return attr.eq(match_value);
	}

}
