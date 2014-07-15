/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class SensorAttribute extends AbstractVaryingAttribute
{
	public SensorAttribute(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.SENSOR;
	}

	public boolean Update(Object new_value)
	{
		return Update(new_value, true);
	}

	private static final SimpleAttribute attribute_type
		= new SimpleAttribute("type", "_derived");
}
