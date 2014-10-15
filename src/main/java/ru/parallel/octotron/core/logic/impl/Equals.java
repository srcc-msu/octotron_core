/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;

public class Equals extends ReactionTemplate
{
	private static final long serialVersionUID = 8157833787643278811L;

	public Equals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		if(!attribute.CheckValid())
			return false;

		return attribute.eq(GetCheckValue());
	}
}
