/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class MatchAprx extends Rule
{
	private final String check_attribute;
	private final Value match_value;
	private final Value aprx;

	public MatchAprx(String check_attribute, Object match_value, Object aprx)
	{
		this.check_attribute = check_attribute;
		this.match_value = Value.Construct(match_value);
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

		return attr.aeq(match_value, aprx);
	}

}
