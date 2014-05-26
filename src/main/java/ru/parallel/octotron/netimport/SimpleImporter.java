/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.netimport;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * threadsafe importer
 * */
public class SimpleImporter implements IImporter
{
	private List<Pair<OctoObject, SimpleAttribute>> data
		= new LinkedList<Pair<OctoObject, SimpleAttribute>>();
	private final Object lock = new Object(); // personal lock for each instance

	public void Put(OctoObject object, SimpleAttribute value)
	{
		synchronized(lock)
		{
			data.add(Pair.of(object, value));
		}
	}

	@Override
	public List<Pair<OctoObject, SimpleAttribute>> Get(int max_count)
	{
		synchronized (lock)
		{
			if(data.size() > max_count)
			{
				List<Pair<OctoObject, SimpleAttribute>> out
					= data.subList(0, max_count);

				data = new LinkedList<Pair<OctoObject, SimpleAttribute>>
					(data.subList(max_count + 1, data.size()));

				return out;
			}
			else
			{
				List<Pair<OctoObject, SimpleAttribute>> out = data;
				data = new LinkedList<Pair<OctoObject, SimpleAttribute>>();

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
