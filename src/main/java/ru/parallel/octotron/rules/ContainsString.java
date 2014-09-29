/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class ContainsString extends Rule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String attribute;
	private final String match_str;

	public ContainsString(String name, String attribute, String match_str)
	{
		super(name) ;
		this.attribute = attribute;
		this.match_str = match_str;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(attribute);

		if(!attr.IsValid())
			return GetDefaultValue();

		return attr.GetString().contains(match_str);
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
