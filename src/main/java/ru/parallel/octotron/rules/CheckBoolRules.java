/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class CheckBoolRules extends OctoRule
{
	private static final long serialVersionUID = -5698688420213900355L;
	private final String[] check_list;

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
	public Object Compute(OctoEntity entity)
	{
		int sum = 0;

		for(String attr_name : check_list)
		{
			OctoAttribute attr = entity.GetAttribute(attr_name);

			if(!attr.GetBoolean() && attr.IsValid() && attr.GetCTime() != 0)
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
