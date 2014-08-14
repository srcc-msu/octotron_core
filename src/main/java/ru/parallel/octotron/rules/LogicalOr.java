/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.OctoRule;

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
	public AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<ModelAttribute> result = new AttributeList<>();

		for(String attr_name : attributes)
			result.add(entity.GetAttribute(attr_name));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		boolean res = false;

		for(String attr_name : attributes)
		{
			ModelAttribute attr = entity.GetAttribute(attr_name);

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
