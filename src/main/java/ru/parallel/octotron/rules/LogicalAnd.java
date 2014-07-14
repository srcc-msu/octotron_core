/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class LogicalAnd extends OctoRule
{
	private static final long serialVersionUID = -5698688420213900355L;
	private final String[] attributes;

	public LogicalAnd(String name, String... attributes)
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
	public Object Compute(ModelEntity entity)
	{
		boolean res = true;

		for(String attr_name : attributes)
		{
			ModelAttribute attr = entity.GetAttribute(attr_name);

			if(attr.IsValid() && attr.GetCTime() != 0)
				res = res & attr.GetBoolean();
		}

		return res;
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
