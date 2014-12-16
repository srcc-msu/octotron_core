/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class ContainsString extends Rule
{
	private final String attribute;
	private final String match_str;

	public ContainsString(String attribute, String match_str)
	{
		this.attribute = attribute;
		this.match_str = match_str;
	}

	@Override
	protected AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(attribute);

		if(!attr.GetValue().IsValid())
			return Value.invalid;

		return attr.GetString().contains(match_str);
	}
}
