/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.ReactionObject;
import ru.parallel.octotron.core.model.impl.meta.ReactionObjectFactory;
import ru.parallel.octotron.core.model.impl.meta.SensorObject;

import java.util.List;

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
		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		for(ReactionObject reaction_object : reaction_objects)
			reaction_object.Repeat(new_value);

		return Update(new_value, true);
	}
}
