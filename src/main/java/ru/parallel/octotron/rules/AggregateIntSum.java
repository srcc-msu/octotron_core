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

public class AggregateIntSum extends OctoRule
{
	private static final long serialVersionUID = -1961148475047706792L;
	private final String detect_str;

	public AggregateIntSum(String attr, String detect_str)
	{
		super(attr);
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

		for(OctoObject obj : object.GetOutNeighbors("type", "contain"))
			for(OctoAttribute att : obj.GetAttributes())
				if(att.GetName().contains(detect_str) && att.IsValid() && att.GetTime() != 0)
					sum += att.GetLong();

		return sum;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
