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

public class AggregateIntSum extends OctoObjectRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final String detect_str;

	public AggregateIntSum(String attribute_name, String detect_str)
	{
		super(attribute_name);
		this.detect_str = detect_str;
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
					sum += att.GetLong();

		return sum;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
