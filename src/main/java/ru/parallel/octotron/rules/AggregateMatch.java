/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoObjectRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class AggregateMatch extends OctoObjectRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final String detect_str;
	private final Object match;

	public AggregateMatch(String attribute_name, String detect_str, Object match)
	{
		super(attribute_name);
		this.detect_str = detect_str;
		this.match = match;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.OUT;
	}

	@Override
	public Object Compute(OctoObject object)
	{
		int sum = 0;

		for(OctoObject obj : object.GetOutNeighbors().Uniq())
			for(OctoAttribute att : obj.GetAttributes())
				if(att.GetName().contains(detect_str) && att.IsValid() && att.GetCTime() != 0)
					if(att.eq(match))
						sum++;

		return sum;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
