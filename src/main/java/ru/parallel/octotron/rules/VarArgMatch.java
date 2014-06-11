/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class VarArgMatch extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final String match_attribute;

	public VarArgMatch(String name, String check_attribute, String match_attribute)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(check_attribute);
		OctoAttribute match_attr = entity.GetAttribute(match_attribute);

		if(attr.GetCTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		if(match_attr.GetCTime() == 0 || !match_attr.IsValid())
			return GetDefaultValue();

		return attr.eq(match_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
