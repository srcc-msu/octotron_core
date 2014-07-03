/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.utils.OctoEntityList;

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
	public OctoEntityList Process(List<Pair<OctoEntity, SimpleAttribute>> packet)
	{
		OctoEntityList changed = new OctoEntityList();

		for(Pair<OctoEntity, SimpleAttribute> sensor : packet)
		{
			OctoEntity entity = sensor.getLeft();

			if(entity.GetAttribute(sensor.getRight().GetName())
				.Update(sensor.getRight().GetValue(), true))
					changed.add(entity);
		}

		return changed;
	}
}
