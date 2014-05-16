/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;

import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.core.OctoRule;
import main.java.ru.parallel.octotron.primitive.EDependencyType;

public class CheckBoolRules extends OctoRule
{
	private static final long serialVersionUID = -5698688420213900355L;
	private String[] check_list;

	public CheckBoolRules(String attr, String... check_list)
	{
		super(attr);
		this.check_list =  ArrayUtils.clone(check_list);
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoObject object)
	{
		int sum = 0;

		for(String attr_name : check_list)
		{
			OctoAttribute attr = object.GetAttribute(attr_name);

			if(!attr.GetBoolean() && attr.IsValid() && attr.GetTime() != 0)
				sum++;
		}

		return sum;
	}

	@Override
	public Object GetDefaultValue()
	{
		return 0;
	}
}
