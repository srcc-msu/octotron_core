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

public class VarArgMatchAprx extends Rule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;
	private final String match_attribute;
	private final Object aprx;

	public VarArgMatchAprx(String name, String check_attribute, String match_attribute, Object aprx)
	{
		super(name) ;
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
		this.aprx = aprx;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(entity.GetMetaAttribute(check_attribute));
		result.add(entity.GetMetaAttribute(match_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(check_attribute);
		IMetaAttribute match_attr = entity.GetMetaAttribute(match_attribute);

		if(!attr.IsValid() || attr.GetCTime() == 0)
			return null;

		if(!match_attr.IsValid() || match_attr.GetCTime() == 0)
			return null;

		return attr.aeq(match_attr.GetValue(), aprx);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
