/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.primitive.EDependencyType;

public class ContainsString extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String param;
	private final String match_str;

	public ContainsString(String attribute_name, String param, String match_str)
	{
		super(attribute_name);
		this.param = param;
		this.match_str = match_str;
	}

	@Override
	public EDependencyType GetDeps()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		OctoAttribute attr = entity.GetAttribute(param);

		if(attr.GetCTime() == 0 || !attr.IsValid())
			return GetDefaultValue();

		return attr.GetString().contains(match_str);
	}

	@Override
	public Object GetDefaultValue()
	{
		return false;
	}
}
