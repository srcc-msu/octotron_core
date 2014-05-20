/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.netimport;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

/**
 * interface for sensor data units
 * */
public interface ISensorData
{
	SimpleAttribute GetValue();

	OctoObject Resolve(GraphService graph_service);

	boolean Check();
}
