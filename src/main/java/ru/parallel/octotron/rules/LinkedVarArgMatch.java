/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.impl.LinkRule;
import ru.parallel.octotron.core.model.ModelLink;

public class LinkedVarArgMatch extends LinkRule
{
	private final String check_attribute;

	public LinkedVarArgMatch(String check_attribute)
	{
		this.check_attribute = check_attribute;
	}

	@Override
	protected AttributeList<IModelAttribute> GetDependency(ModelLink link)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(link.GetObjects().get(0).GetAttribute(check_attribute));
		result.add(link.GetObjects().get(1).GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelLink link)
	{
		IModelAttribute attr1 = link.GetObjects().get(0).GetAttribute(check_attribute);
		IModelAttribute attr2 = link.GetObjects().get(1).GetAttribute(check_attribute);

		if(!attr1.GetValue().IsValid() || !attr2.GetValue().IsValid())
			return Value.invalid;

		return attr1.eq(attr2.GetValue());
	}

}
