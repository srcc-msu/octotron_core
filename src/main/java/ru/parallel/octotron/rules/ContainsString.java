/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.OctoRule;

public class ContainsString extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String attribute;
	private final String match_str;

	public ContainsString(String name, String attribute, String match_str)
	{
		super(name) ;
		this.attribute = attribute;
		this.match_str = match_str;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(attribute);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.GetString().contains(match_str);
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
