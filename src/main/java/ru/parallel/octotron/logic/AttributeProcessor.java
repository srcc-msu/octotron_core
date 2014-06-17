/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.utils.OctoObjectList;

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
	 * return list of values, that changed<br>
	 * */
	public OctoObjectList Process(List<Pair<OctoObject, SimpleAttribute>> packet)
	{
		OctoObjectList changed = new OctoObjectList();

		for(Pair<OctoObject, SimpleAttribute> sensor : packet)
		{
			OctoObject obj = sensor.getLeft();

			if(obj.GetAttribute(sensor.getRight().GetName())
				.Update(sensor.getRight().GetValue(), true))
					changed.add(obj);
		}

		return changed;
	}
}
