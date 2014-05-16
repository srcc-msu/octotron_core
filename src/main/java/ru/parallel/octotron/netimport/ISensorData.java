/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.netimport;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

/**
 * interface for sensor data units
 * */
public interface ISensorData
{
	SimpleAttribute GetValue();

	OctoObject Resolve(GraphService graph_service);

	boolean Check();
}
