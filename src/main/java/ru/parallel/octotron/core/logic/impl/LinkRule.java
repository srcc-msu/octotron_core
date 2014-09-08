/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;

public abstract class LinkRule extends Rule
{
	private static final long serialVersionUID = -943099846881874234L;

	protected LinkRule(String name)
	{
		super(name);
	}

	@Override
	public final Object Compute(ModelEntity entity)
	{
		return Compute((ModelLink) entity);
	}

	public abstract Object Compute(ModelLink object);

	@Override
	public final AttributeList<IMetaAttribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelLink) entity);
	}

	public abstract AttributeList<IMetaAttribute> GetDependency(ModelLink object);

}
