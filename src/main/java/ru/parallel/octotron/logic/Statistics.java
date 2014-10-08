/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.utils.Timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Statistics
{
	public static class Stat
	{
		public final String name;

		public Metric queue = new Metric();
		public Metric total = new Metric();

		public Stat(String name)
		{
			this.name = name;
		}
	}

	Map<String, Stat> stats = new HashMap<>();

	private final Timer timer_60 = new Timer();

	public Statistics()
	{
		timer_60.Start();
	}

	public void Add(String name, int add, int queue)
	{
		Stat stat = stats.get(name);

		if(stat == null)
		{
			stat = new Stat(name);
			stats.put(name, stat);
		}

		stat.total.Collect(add);
		stat.queue.Collect(queue);
	}

	private static Map<String, Object> GetAvgs(Metric metric, String name)
	{
		Map<String, Object> res = new HashMap<>();

		res.put(name + " current", String.valueOf(metric.GetCurrent()));
		res.put(name + " min", String.valueOf(metric.GetMin()));
		res.put(name + " max", String.valueOf(metric.GetMax()));
		res.put(name + " avg", String.format("%.2f", metric.GetAvg()));

		return res;
	}

	private static Map<String, Object> GetChange(Metric metric, int seconds, String name)
	{
		Map<String, Object> res = new HashMap<>();

		res.put(name + " for " + seconds + " secs"
			, String.valueOf(metric.GetValue()));

		if(seconds > 0)
			res.put(name + " per sec"
				, String.format("%.2f", 1.0 * metric.GetValue() / seconds));

		return res;
	}

	public List<Map<String, Object>> GetStat()
	{
		List<Map<String, Object>> res = new LinkedList<>();

		for(Stat stat : stats.values())
			res.add(Statistics.GetAvgs(stat.queue, stat.name + " queue"));

		for(Stat stat : stats.values())
			res.add(Statistics.GetChange(stat.total, (int)timer_60.Get(), stat.name + " total"));

		return res;
	}

	public void Process()
	{
		if(timer_60.Get() > 60) /*secs in min..*/
		{
			for(Stat stat : stats.values())
			{
				stat.queue.Reset();
				stat.total.Reset();
			}

			timer_60.Start();
		}
	}
}
