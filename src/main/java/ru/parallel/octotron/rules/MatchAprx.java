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

public class MatchAprx extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final Object match_value;
	private final Object aprx;

	public MatchAprx(String name, String check_attribute, Object match_value, Object aprx)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_value = match_value;
		this.aprx = aprx;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(check_attribute);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.aeq(match_value, aprx);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
