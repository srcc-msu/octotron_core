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
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class ArgMatchAprx extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private String param;
	private String match_arg;
	private Object aprx;

	public ArgMatchAprx(String attr, String param, String match_arg, Object aprx)
	{
		super(attr);
		this.param = param;
		this.match_arg = match_arg;
		this.aprx = aprx;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoObject object)
		throws ExceptionModelFail
	{
		OctoAttribute attr = object.GetAttribute(param);

		if(attr.GetTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.aeq(object.GetAttribute(match_arg).GetValue(), aprx);
	}

	@Override
	public Object GetDefaultValue()
		throws ExceptionModelFail
	{
		return true;
	}
}
