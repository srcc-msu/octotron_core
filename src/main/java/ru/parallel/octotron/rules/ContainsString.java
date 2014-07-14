/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.EDependencyType;

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
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
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
