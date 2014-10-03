/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.impl.LinkRule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;

public class LinkedVarArgMatch extends LinkRule
{
	private final String check_attribute;

	public LinkedVarArgMatch(String name, String check_attribute)
	{
		super(name) ;
		this.check_attribute = check_attribute;
	}

	@Override
	public AttributeList<IModelAttribute> GetDependency(ModelLink link)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(link.Target().GetAttribute(check_attribute));
		result.add(link.Source().GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelLink link)
	{
		IModelAttribute target_attr = link.Target().GetAttribute(check_attribute);
		IModelAttribute source_attr = link.Source().GetAttribute(check_attribute);

		if(!target_attr.IsValid())
			return null;

		if(!source_attr.IsValid())
			return null;

		return target_attr.eq(source_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
