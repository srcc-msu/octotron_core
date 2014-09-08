/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic.importer;

import org.apache.commons.lang3.tuple.Pair;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.LinkedList;
import java.util.List;

/**
 * threadsafe importer
 * */
public class SimpleImporter implements IImporter
{
	private List<Pair<ModelEntity, SimpleAttribute>> data
		= new LinkedList<>();
	private final Object lock = new Object(); // personal lock for each instance

	public void Put(ModelEntity entity, SimpleAttribute value)
	{
		synchronized(lock)
		{
			data.add(Pair.of(entity, value));
		}
	}

	@Override
	public List<Pair<ModelEntity, SimpleAttribute>> Get(int max_count)
	{
		synchronized (lock)
		{
			if(data.size() > max_count)
			{
				List<Pair<ModelEntity, SimpleAttribute>> out
					= data.subList(0, max_count);

				data = new LinkedList<>
					(data.subList(max_count + 1, data.size()));

				return out;
			}
			else
			{
				List<Pair<ModelEntity, SimpleAttribute>> out = data;
				data = new LinkedList<>();

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
