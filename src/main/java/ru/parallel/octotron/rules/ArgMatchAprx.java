/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class ArgMatchAprx extends Rule
{
	private final String check_attribute;
	private final String match_attribute;
	private final Value aprx;

	public ArgMatchAprx(String check_attribute, String match_attribute, Object aprx)
	{
		this.check_attribute = check_attribute;
		this.match_attribute = match_attribute;
		this.aprx = Value.Construct(aprx);
	}

	@Override
	protected AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		result.add(entity.GetAttribute(check_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(check_attribute);

		return attr.aeq(entity.GetAttribute(match_attribute).GetValue(), aprx);
	}

}
