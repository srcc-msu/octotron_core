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

public class Match extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final Object match_value;

	public Match(String name, String check_attribute, Object match_value)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_value = match_value;
	}

	@Override
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(check_attribute);

		if(attr.GetCTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.eq(match_value);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
