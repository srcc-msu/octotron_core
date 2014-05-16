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

public class LowerArgThreshold extends OctoRule
{
	private static final long serialVersionUID = 2191189080156471873L;
	private final String param;
	private final String arg_threshold;

	public LowerArgThreshold(String attr, String param, String arg_threshold)
	{
		super(attr);
		this.param = param;
		this.arg_threshold = arg_threshold;
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

		return attr.gt(object.GetAttribute(arg_threshold).GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
