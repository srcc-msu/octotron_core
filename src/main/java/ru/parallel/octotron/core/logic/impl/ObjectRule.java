/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;


import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;

public abstract class ObjectRule extends Rule
{
	private static final long serialVersionUID = -1936097371431183834L;

	protected ObjectRule(String name)
	{
		super(name);
	}

	@Override
	public final Object Compute(ModelEntity entity)
	{
		return Compute((ModelObject) entity);
	}

	public abstract Object Compute(ModelObject object);

	@Override
	public final AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelObject) entity);
	}

	public abstract AttributeList<IModelAttribute> GetDependency(ModelObject object);
}
