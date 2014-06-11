/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

public abstract class OctoObjectRule extends OctoRule
{
	private static final long serialVersionUID = -1936097371431183834L;

	protected OctoObjectRule(String name)
	{
		super(name);
	}

	@Override
	public Object Compute(OctoEntity entity)
	{
		return Compute((OctoObject)entity);
	}

	public abstract Object Compute(OctoObject object);
}
