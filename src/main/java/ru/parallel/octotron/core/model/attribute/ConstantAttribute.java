/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

public class ConstantAttribute extends ModelAttribute
{
	public ConstantAttribute(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	@Override
	public AttributeList<DerivedAttribute> GetDependant()
	{
		return new AttributeList<>();
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.CONSTANT;
	}
}
