/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.rule;

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
	public Object Compute(ModelEntity entity)
	{
		return Compute((ModelObject) entity);
	}

	public abstract Object Compute(ModelObject object);
}
