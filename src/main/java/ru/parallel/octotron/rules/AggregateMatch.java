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

public class AggregateMatch extends OctoRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private String detect_str;
	private Object match;

	public AggregateMatch(String attr, String detect_str, Object match)
	{
		super(attr);
		this.detect_str = detect_str;
		this.match = match;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.ALL;
	}

	@Override
	public Object Compute(OctoObject object)
	{
		int sum = 0;

		for(OctoObject obj : object.GetOutNeighbors("type", "contain"))
			for(OctoAttribute att : obj.GetAttributes())
				if(att.GetName().contains(detect_str) && att.IsValid() && att.GetTime() != 0)
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
