/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.model.ModelEntity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TimerProcessor
{
	private static final class Entry
	{
		public final ModelEntity entity;
		public final String name;

		public Entry(ModelEntity entity, String name)
		{
			this.entity = entity;
			this.name = name;
		}
	}

	public static final TimerProcessor INSTANCE = new TimerProcessor();

	private final List<Entry> timers;

	private TimerProcessor()
	{
		timers = new LinkedList<>();
	}

	public static void AddTimer(ModelEntity entity, String name)
	{
		TimerProcessor.INSTANCE.timers.add(new Entry(entity, name));
	}



	public static void RemoveTimer(ModelEntity entity, String name)
	{
		Iterator<Entry> it = TimerProcessor.INSTANCE.timers.iterator();

		while (it.hasNext())
		{
			Entry entry = it.next();

			if(entry.entity.equals(entity) && entry.name.equals(name))
				it.remove();
		}
	}
}
