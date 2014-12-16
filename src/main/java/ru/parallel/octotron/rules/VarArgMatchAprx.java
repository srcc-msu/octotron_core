/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;

public class VarArgMatchAprx extends Rule
{
	private final String check_attribute;
	private final String match_attribute;
	private final Value aprx;

	public VarArgMatchAprx(String check_attribute, String match_attribute, Object aprx)
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
		result.add(entity.GetAttribute(match_attribute));

		return result;
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		IModelAttribute attr = entity.GetAttribute(check_attribute);
		IModelAttribute match_attr = entity.GetAttribute(match_attribute);

		if(!attr.GetValue().IsValid() || !match_attr.GetValue().IsValid())
			return Value.invalid;

		return attr.aeq(match_attr.GetValue(), aprx);
	}

}
