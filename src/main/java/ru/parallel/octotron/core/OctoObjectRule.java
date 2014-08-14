/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;

public abstract class OctoObjectRule extends OctoRule
{
	private static final long serialVersionUID = -1936097371431183834L;

	protected OctoObjectRule(String name)
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
	public final AttributeList<ModelAttribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelObject) entity);
	}

	public abstract AttributeList<ModelAttribute> GetDependency(ModelObject object);
}
