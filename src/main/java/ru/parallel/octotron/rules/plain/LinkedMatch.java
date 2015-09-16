/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules.plain;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.rules.LinkRule;

public class LinkedMatch extends LinkRule
{
	private final String check_attribute;

	public LinkedMatch(String check_attribute)
	{
		this.check_attribute = check_attribute;
	}

	@Override
	public AttributeList<Attribute> GetDependency(ModelLink link)
	{
		AttributeList<Attribute> result = new AttributeList<>();

		result.add(link.GetObjects().get(0).GetAttribute(check_attribute));
		result.add(link.GetObjects().get(1).GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelLink link)
	{
		Attribute attr1 = link.GetObjects().get(0).GetAttribute(check_attribute);
		Attribute attr2 = link.GetObjects().get(1).GetAttribute(check_attribute);

		if(!attr1.IsValid() || !attr2.IsValid())
			return Value.invalid;

		return attr1.eq(attr2);
	}

}
