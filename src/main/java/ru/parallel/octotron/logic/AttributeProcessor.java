/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.attribute.SensorAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * Processes input data<br>
 * provides mapping from some sensor values to the graph model -<br>
 * object, that holds it and some additional info, probably<br>
 * */
public class AttributeProcessor
{
	/**
	 * process each sensor value from the \packet<br>
	 * return list of sensors, that changed<br>
	 * */
	public AttributeList<SensorAttribute> Process(List<Pair<ModelEntity, SimpleAttribute>> packets)
	{
		AttributeList<SensorAttribute> changed = new AttributeList<>();

		for(Pair<ModelEntity, SimpleAttribute> packet : packets)
		{
			ModelEntity entity = packet.getLeft();

			SensorAttribute sensor = entity.GetSensor(packet.getRight().GetName());

			if(sensor.Update(packet.getRight().GetValue()))
				changed.add(sensor);
		}

		return changed;
	}
}
