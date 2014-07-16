/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.meta.AttributeObject;
import ru.parallel.octotron.core.model.meta.SensorObject;

public class SensorAttribute extends AbstractVaryingAttribute<SensorObject>
{
	public SensorAttribute(ModelEntity parent, SensorObject meta, String name)
	{
		super(parent, meta, name);
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
}
