/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.utils.OctoEntityList;
import ru.parallel.utils.JavaUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TimerProcessor
{
	private static final class Entry
	{
		public final OctoEntity entity;
		public final String name;

		public Entry(OctoEntity entity, String name)
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

	public static void AddTimer(OctoEntity entity, String name)
	{
		TimerProcessor.INSTANCE.timers.add(new Entry(entity, name));
	}

	public static OctoEntityList Process()
	{
		OctoEntityList to_update = new OctoEntityList();

		long current_time = JavaUtils.GetTimestamp(); // TODO: move into the cycle?

		Iterator<Entry> it = TimerProcessor.INSTANCE.timers.iterator();

		while (it.hasNext())
		{
			Entry entry = it.next();

			if(entry.entity.IsTimerExpired(entry.name, current_time))
			{
				to_update.add(entry.entity);

				it.remove();
			}
		}

		return to_update;
	}

	public static void RemoveTimer(OctoEntity entity, String name)
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
