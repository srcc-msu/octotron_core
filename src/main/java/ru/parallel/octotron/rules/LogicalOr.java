/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class LogicalOr extends Rule
{
	private static final long serialVersionUID = -5698688420213900355L;
	private final String[] attributes;

	public LogicalOr(String name, String... attributes)
	{
		super(name) ;
		this.attributes = ArrayUtils.clone(attributes);
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		for(String attr_name : attributes)
			result.add(entity.GetMetaAttribute(attr_name));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		boolean res = false;

		for(String attr_name : attributes)
		{
			IMetaAttribute attr = entity.GetMetaAttribute(attr_name);

			if(!attr.IsValid() || attr.GetCTime() == 0)
				return null;

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
