/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.logic;

import java.util.List;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.netimport.ISensorData;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionImportFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.ObjectList;

/**
 * Processes input data<br>
 * provides mapping from some sensor values to the graph model -<br>
 * object, that holds it and some additional info, probably<br>
 * */
public class AttributeProcessor
{
	private GraphService graph_service;

	public AttributeProcessor(GraphService graph_service)
	{
		this.graph_service = graph_service;
	}

	/**
	 * process each sensor value from the \packet<br>
	 * return list of values, that changed<br>
	 * */
	public ObjectList Process(List<? extends ISensorData> packet)
		throws ExceptionModelFail, ExceptionDBError, ExceptionImportFail
	{
		ObjectList changed = new ObjectList();

		for(ISensorData sensor : packet)
		{
			OctoObject obj = sensor.Resolve(graph_service);

			if(obj.UpdateAttribute(sensor.GetValue()))
				changed.add(obj);
		}

		return changed;
	}
}
