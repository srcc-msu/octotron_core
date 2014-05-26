/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.utils.OctoAttributeList;
import ru.parallel.utils.JavaUtils;

public final class TimerProcessor
{
	public static final TimerProcessor INSTANCE = new TimerProcessor();

	private final List<OctoAttribute> timers;

	private TimerProcessor()
	{
		timers = new LinkedList<>();
	}

	public static void AddTimer(OctoAttribute timer)
	{
		TimerProcessor.INSTANCE.timers.add(timer);
	}

	public static OctoAttributeList Process()
	{
		OctoAttributeList timers_timed_out = new OctoAttributeList();

		long cur_time = JavaUtils.GetTimestamp(); // TODO: move into the cycle?

		Iterator<OctoAttribute> it = TimerProcessor.INSTANCE.timers.iterator();

		while (it.hasNext())
		{
			OctoAttribute timer = it.next();
			long set_time = timer.GetCTime();

			if(cur_time - set_time > timer.GetLong())
			{
				timer.Update(0, true);
				timers_timed_out.add(timer);

				it.remove();
			}
		}

		return timers_timed_out;
	}
}
