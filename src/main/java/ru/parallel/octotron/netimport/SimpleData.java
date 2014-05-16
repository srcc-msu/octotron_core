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

public class SimpleData implements ISensorData
{
	private final OctoObject object;
	private final SimpleAttribute value;

	public SimpleData(OctoObject object, SimpleAttribute value)
	{
		this.object = object;
		this.value = value;
	}

	@Override
	public SimpleAttribute GetValue()
		throws ExceptionModelFail
	{
		return value;
	}

	@Override
	public OctoObject Resolve(GraphService graph_service)
	{
		return object;
	}

	@Override
	public boolean Check()
	{
		return true;
	}

}
