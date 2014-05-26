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

public class LocalErrors extends OctoRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final String detect_str;

	public LocalErrors(String attribute_name, String detect_str)
	{
		super(attribute_name);
		this.detect_str = detect_str;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		int sum = 0;

		for(OctoAttribute attr : entity.GetAttributes())
			if(attr.GetName().contains(detect_str) && attr.IsValid() && attr.GetCTime() != 0)
				sum += attr.GetLong();

		return sum;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
