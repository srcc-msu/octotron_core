/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.rule.OctoLinkRule;

public class LinkedVarArgMatch extends OctoLinkRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;

	public LinkedVarArgMatch(String name, String check_attribute)
	{
		super(name) ;
		this.check_attribute = check_attribute;
	}

	@Override
	public AttributeList<ModelAttribute> GetDependency(ModelLink link)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		result.add(link.Target().GetAttribute(check_attribute));
		result.add(link.Source().GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelLink link)
	{
		ModelAttribute target_attr = link.Target().GetAttribute(check_attribute);
		ModelAttribute source_attr = link.Source().GetAttribute(check_attribute);

		if(!target_attr.IsValid())
			return GetDefaultValue();

		if(!source_attr.IsValid())
			return GetDefaultValue();

		return target_attr.eq(source_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
