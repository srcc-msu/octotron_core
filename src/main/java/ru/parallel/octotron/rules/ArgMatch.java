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

public class ArgMatch extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String param;
	private final String match_arg;

	public ArgMatch(String attr, String param, String match_arg)
	{
		super(attr);
		this.param = param;
		this.match_arg = match_arg;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(param);

		if(attr.GetCTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.eq(entity.GetAttribute(match_arg).GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
