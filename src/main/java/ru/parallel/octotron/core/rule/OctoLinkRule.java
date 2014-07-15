/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.rule;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;

public abstract class OctoLinkRule extends OctoRule
{
	private static final long serialVersionUID = -943099846881874234L;

	protected OctoLinkRule(String name)
	{
		super(name);
	}

	@Override
	public Object Compute(ModelEntity entity)
	{
		return Compute((ModelLink) entity);
	}

	public abstract Object Compute(ModelLink link);
}
