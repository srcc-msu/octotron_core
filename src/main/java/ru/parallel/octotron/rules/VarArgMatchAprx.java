/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;

public class VarArgMatchAprx extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final String match_attribute;
	private final Object aprx;

	public VarArgMatchAprx(String name, String check_attribute, String match_attribute, Object aprx)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
		this.aprx = aprx;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(check_attribute));
		result.add(entity.GetAttribute(match_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(check_attribute);
		ModelAttribute match_attr = entity.GetAttribute(match_attribute);

		if(!attr.IsValid())
			return GetDefaultValue();

		if(!match_attr.IsValid())
			return GetDefaultValue();

		return attr.aeq(match_attr.GetValue(), aprx);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
