/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

public abstract class OctoLinkRule extends OctoRule
{
	protected OctoLinkRule(String attribute_name)
	{
		super(attribute_name);
	}

	public Object Compute(OctoEntity entity)
	{
		return Compute((OctoLink) entity);
	}

	public abstract Object Compute(OctoLink link);
}
