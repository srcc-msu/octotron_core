/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;


import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.collections.AttributeList;

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
	public AttributeList<SensorAttribute> Process(List<ImportManager.Packet> packets)
	{
		AttributeList<SensorAttribute> changed = new AttributeList<>();

		for(ImportManager.Packet packet : packets)
		{
			SensorAttribute sensor = packet.object.GetSensor(packet.attribute.GetName());

			sensor.Update(packet.attribute.GetValue());
			changed.add(sensor);
		}

		return changed;
	}
}
