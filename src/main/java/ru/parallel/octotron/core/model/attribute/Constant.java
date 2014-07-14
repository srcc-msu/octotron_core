/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class Constant extends ModelAttribute
{
	private Constant(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	public static final Constant TryConstruct(ModelEntity parent, String name)
	{
		return new Constant(parent, name);
	}

	@Override
	public AttributeList<Derived> GetDependant()
	{
		return null;
	}
}
