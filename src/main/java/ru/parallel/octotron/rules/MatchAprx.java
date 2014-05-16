/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.rules;

import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.core.OctoRule;
import main.java.ru.parallel.octotron.primitive.EDependencyType;

public class MatchAprx extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private String param;
	private Object match_value;
	private Object aprx;

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
	public Object Compute(OctoObject object)
	{
		OctoAttribute attr = object.GetAttribute(param);

		if(attr.GetTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.aeq(match_value, aprx);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
