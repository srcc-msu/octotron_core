/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;


import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;

public abstract class ObjectRule extends Rule
{
	protected ObjectRule() {}

	@Override
	public final Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		return Compute((ModelObject) entity, rule_attribute);
	}

	public abstract Object Compute(ModelObject object, Attribute rule_attribute);

	@Override
	public final AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelObject) entity);
	}

	public abstract AttributeList<Attribute> GetDependency(ModelObject object);
}
