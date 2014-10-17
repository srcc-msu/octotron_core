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
import ru.parallel.octotron.core.model.ModelLink;

public abstract class LinkRule extends Rule
{
	protected LinkRule() {}

	@Override
	public final Object Compute(ModelEntity entity)
	{
		return Compute((ModelLink) entity);
	}

	public abstract Object Compute(ModelLink object);

	@Override
	public final AttributeList<IModelAttribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelLink) entity);
	}

	public abstract AttributeList<IModelAttribute> GetDependency(ModelLink object);
}
