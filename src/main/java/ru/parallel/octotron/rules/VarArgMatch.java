/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.core.primitive.EDependencyType;

public class VarArgMatch extends OctoRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final String match_attribute;

	public VarArgMatch(String name, String check_attribute, String match_attribute)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
	}

	@Override
	public EDependencyType GetDependency()
	{
		return EDependencyType.SELF;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(check_attribute);
		ModelAttribute match_attr = entity.GetAttribute(match_attribute);

		if(!attr.IsValid())
			return GetDefaultValue();

		if(!match_attr.IsValid())
			return GetDefaultValue();

		return attr.eq(match_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
