/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.netimport;

import java.util.LinkedList;
import java.util.List;

import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * threadsafe importer
 * */
public class SimpleImporter implements IImporter
{
	private List<SimpleData> data = new LinkedList<SimpleData>();
	private final Object lock = new Object(); // personal lock for each instance

	public void Put(OctoObject object, SimpleAttribute value)
	{
		synchronized(lock)
		{
			data.add(new SimpleData(object, value));
		}
	}

	@Override
	public List<? extends ISensorData> Get(int max_count)
	{
		synchronized (lock)
		{
			if(data.size() > max_count)
			{
				List<SimpleData> out = data.subList(0, max_count);
				data = new LinkedList<SimpleData>(data.subList(max_count + 1, data.size()));

				return out;
			}
			else
			{
				List<SimpleData> out = data;
				data = new LinkedList<SimpleData>();

				return out;
			}
		}
	}

	@Override
	public int GetSize()
	{
		synchronized (lock)
		{
			return data.size();
		}
	}
}
