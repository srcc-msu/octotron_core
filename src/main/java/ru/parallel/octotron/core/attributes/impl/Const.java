/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;

import java.util.Map;

public final class Const extends Attribute
{
	public Const(ModelEntity parent, String name, Value value)
	{
		super(EAttributeType.CONST, parent, name, value);
	}

	@Override
	protected void UpdateSelf()
	{
		// nothing to see here
	}
}