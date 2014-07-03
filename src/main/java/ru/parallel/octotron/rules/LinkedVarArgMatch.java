/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.*;
import ru.parallel.octotron.primitive.EDependencyType;

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
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoLink link)
	{
		OctoAttribute target_attr = link.Target().GetAttribute(check_attribute);
		OctoAttribute source_attr = link.Source().GetAttribute(check_attribute);

		if(target_attr.GetCTime() == 0 || !target_attr.IsValid())
			return GetDefaultValue();

		if(source_attr.GetCTime() == 0 || !source_attr.IsValid())
			return GetDefaultValue();

		return target_attr.eq(source_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
