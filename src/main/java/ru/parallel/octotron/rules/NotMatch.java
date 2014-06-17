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

public class NotMatch extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String param;
	private final Object match_value;

	public NotMatch(String name, String param, Object match_value)
	{
		super(name) ;
		this.param = param;
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
		OctoAttribute attr = entity.GetAttribute(param);

		if(attr.GetCTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.ne(match_value);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
