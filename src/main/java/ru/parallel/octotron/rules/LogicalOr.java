/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class LogicalOr extends OctoRule
{
	private static final long serialVersionUID = -5698688420213900355L;
	private final String[] attributes;

	public LogicalOr(String name, String... attributes)
	{
		super(name) ;
		this.attributes = ArrayUtils.clone(attributes);
	}

	@Override
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		boolean res = false;

		for(String attr_name : attributes)
		{
			OctoAttribute attr = entity.GetAttribute(attr_name);

			if(attr.IsValid() && attr.GetCTime() != 0)
				res = res | attr.GetBoolean();
		}

		return res;
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
