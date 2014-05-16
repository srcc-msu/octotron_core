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

public class LowerThreshold extends OctoRule
{
	private static final long serialVersionUID = 2678310930260346638L;
	private final String param;
	private final Object threshold;

	public LowerThreshold(String attr, String param, Object threshold)
	{
		super(attr);
		this.param = param;
		this.threshold = threshold;
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

		return attr.gt(threshold);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
