/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.logic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.AttributeList;
import main.java.ru.parallel.utils.JavaUtils;

public final class TimerProcessor
{
	public static final TimerProcessor INSTANCE = new TimerProcessor();

	private List<OctoAttribute> timers;

	private TimerProcessor()
	{
		timers = new LinkedList<OctoAttribute>();
	}

	public static void AddTimer(OctoAttribute timer)
	{
		INSTANCE.timers.add(timer);
	}

	public static AttributeList Process()
		throws ExceptionModelFail, ExceptionDBError
	{
		AttributeList timers_timed_out = new AttributeList();

		long cur_time = JavaUtils.GetTimestamp(); // TODO: move into the cycle?

		Iterator<OctoAttribute> it = INSTANCE.timers.iterator();

		while (it.hasNext())
		{
			OctoAttribute timer = it.next();
			long set_time = timer.GetTime();

			if(cur_time - set_time > timer.GetLong())
			{
				timer.Update(0);
				timers_timed_out.add(timer);

				it.remove();
			}
		}

		return timers_timed_out;
	}
}
