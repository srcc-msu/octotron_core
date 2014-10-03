/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic.importer;

import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.logic.ImportManager;

import java.util.LinkedList;
import java.util.List;

/**
 * threadsafe importer
 * */
public class SimpleImporter implements IImporter
{
	private List<ImportManager.Packet> data
		= new LinkedList<>();
	private final Object lock = new Object(); // personal lock for each instance

	public void Put(ModelObject object, SimpleAttribute value)
	{
		synchronized(lock)
		{
			data.add(new ImportManager.Packet(object, value));
		}
	}

	@Override
	public List<ImportManager.Packet> Get(int max_count)
	{
		synchronized (lock)
		{
			if(data.size() > max_count)
			{
				List<ImportManager.Packet> out
					= data.subList(0, max_count);

				data = new LinkedList<>
					(data.subList(max_count + 1, data.size()));

				return out;
			}
			else
			{
				List<ImportManager.Packet> out = data;
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
