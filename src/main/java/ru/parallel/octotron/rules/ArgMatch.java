/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoObject;
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
	public Object Compute(OctoObject object)
	{
		OctoAttribute attr = object.GetAttribute(param);

		if(attr.GetTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.eq(object.GetAttribute(match_arg).GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
