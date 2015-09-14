/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules.plain;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class NotMatchArg extends Rule
{
	private final String check_attribute;
	private final String match_attribute;

	public NotMatchArg(String check_attribute, String match_attribute)
	{
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
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
		Attribute match_attr = entity.GetAttribute(match_attribute);

		if(!attr.IsValid() || !attr.IsValid())
			return Value.invalid;

		return attr.ne(match_attr);
	}
}
