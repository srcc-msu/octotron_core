/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

public abstract class OctoLinkRule extends OctoRule
{
	private static final long serialVersionUID = -943099846881874234L;

	protected OctoLinkRule(String attribute_name)
	{
		super(attribute_name);
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		return Compute((OctoLink) entity);
	}

	public abstract Object Compute(OctoLink link);
}
