/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.impl.LinkRule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelLink;

public class LinkedVarArgMatch extends LinkRule
{
	private static final long serialVersionUID = -665317574895287470L;
	private final String check_attribute;

	public LinkedVarArgMatch(String name, String check_attribute)
	{
		super(name) ;
		this.check_attribute = check_attribute;
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelLink link)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(link.Target().GetMetaAttribute(check_attribute));
		result.add(link.Source().GetMetaAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelLink link)
	{
		IMetaAttribute target_attr = link.Target().GetMetaAttribute(check_attribute);
		IMetaAttribute source_attr = link.Source().GetMetaAttribute(check_attribute);

		if(!target_attr.IsValid())
			return null;

		if(!source_attr.IsValid())
			return null;

		return target_attr.eq(source_attr.GetValue());
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
