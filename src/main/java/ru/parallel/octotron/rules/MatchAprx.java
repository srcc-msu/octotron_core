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

public class MatchAprx extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String param;
	private final Object match_value;
	private final Object aprx;

	public MatchAprx(String attr, String param, Object match_value, Object aprx)
	{
		super(attr);
		this.param = param;
		this.match_value = match_value;
		this.aprx = aprx;
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

		return attr.aeq(match_value, aprx);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
