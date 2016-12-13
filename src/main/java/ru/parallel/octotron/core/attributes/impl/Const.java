/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.attributes.EAttributeType;

/**
 * const attributes do not have any logic
 * they just store a value
 * */
public final class Const extends Attribute
{
	public Const(ModelEntity parent, String name, Value value)
	{
		super(EAttributeType.CONST, parent, name, value, 0);
	}

	@Override
	protected void UpdateSelf(long current_time)
	{
		// nothing to see here
	}
}
