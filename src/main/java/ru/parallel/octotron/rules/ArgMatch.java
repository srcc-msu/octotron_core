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

public class ArgMatch extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private String param;
	private String match_arg;

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
		throws ExceptionModelFail
	{
		OctoAttribute attr = object.GetAttribute(param);

		if(attr.GetTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.eq(object.GetAttribute(match_arg).GetValue());
	}

	@Override
	public Object GetDefaultValue()
		throws ExceptionModelFail
	{
		return true;
	}
}
