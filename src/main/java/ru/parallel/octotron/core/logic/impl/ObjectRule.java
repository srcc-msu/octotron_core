/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;


import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;

public abstract class ObjectRule extends Rule
{
	protected ObjectRule() {}

	@Override
	public final Object Compute(ModelEntity entity)
	{
		return Compute((ModelObject) entity);
	}

	public abstract Object Compute(ModelObject object);

	@Override
	protected final AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelObject) entity);
	}

	protected abstract AttributeList<IModelAttribute> GetDependency(ModelObject object);
}
