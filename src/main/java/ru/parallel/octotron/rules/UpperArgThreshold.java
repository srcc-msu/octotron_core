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

public class UpperArgThreshold extends OctoRule
{
	private static final long serialVersionUID = 2191189080156471873L;
	private final String param;
	private final String arg_threshold;

	public UpperArgThreshold(String name, String param, String arg_threshold)
	{
		super(name) ;
		this.param = param;
		this.arg_threshold = arg_threshold;
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

		return attr.lt(entity.GetAttribute(arg_threshold).GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
