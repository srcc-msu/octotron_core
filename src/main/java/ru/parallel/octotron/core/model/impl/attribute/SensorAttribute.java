/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.SensorObject;

public class SensorAttribute extends AbstractVaryingAttribute<SensorObject>
{
	public SensorAttribute(ModelEntity parent, GraphAttribute attribute, SensorObject meta)
	{
		super(parent, attribute, meta);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.SENSOR;
	}

	public boolean Update(Object new_value)
	{
		return InnerUpdate(new_value);
	}
}
