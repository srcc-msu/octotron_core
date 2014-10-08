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
	private final Metric request_queue_60 = new Metric();
	private final Metric http_queue_60 = new Metric();
	private final Metric import_queue_60 = new Metric();

	private final Metric request_processed_60 = new Metric();
	private final Metric http_processed_60 = new Metric();
	private final Metric import_processed_60 = new Metric();

	private final Timer timer_60 = new Timer();

	public Statistics()
	{
		timer_60.Start();
	}

	public void Request(int add, int queue)
	{
		request_processed_60.Collect(add);
		request_queue_60.Collect(queue);
	}

	public void Http(int add, int queue)
	{
		http_processed_60.Collect(add);
		http_queue_60.Collect(queue);
	}

	public void Import(int add, int queue)
	{
		import_processed_60.Collect(add);
		import_queue_60.Collect(queue);
	}

	private static Map<String, Object> GetAvgs(Metric metric, String name)
	{
		Map<String, Object> res = new HashMap<>();

		res.put(name + " avg", String.format("%.2f", metric.GetAvg()));
		res.put(name + " min", String.valueOf(metric.GetMin()));
		res.put(name + " max", String.valueOf(metric.GetMax()));

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

		res.add(Statistics.GetAvgs(request_queue_60, "request queue"));
		res.add(Statistics.GetAvgs(http_queue_60, "http queue"));
		res.add(Statistics.GetAvgs(import_queue_60, "import queue"));

		res.add(Statistics.GetChange(request_processed_60, (int)timer_60.Get(), "requests"));
		res.add(Statistics.GetChange(http_processed_60, (int)timer_60.Get(), "http"));
		res.add(Statistics.GetChange(import_processed_60, (int)timer_60.Get(), "imported"));

		return res;
	}

	public void Process()
	{
		if(timer_60.Get() > 60) /*secs in min..*/
		{
			request_queue_60.Reset();
			http_queue_60.Reset();
			import_queue_60.Reset();

			request_processed_60.Reset();
			http_processed_60.Reset();
			import_processed_60.Reset();

			timer_60.Start();
		}
	}
}
