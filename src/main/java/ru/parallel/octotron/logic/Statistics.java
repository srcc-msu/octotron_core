/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.utils.Timer;

public class Statistics
{
	private final Metric request_queue_60 = new Metric();
	private final Metric blocking_request_queue_60 = new Metric();
	private final Metric import_queue_60 = new Metric();

	private final Metric sleep_60 = new Metric();

	private final Metric request_processed_60 = new Metric();
	private final Metric blocking_request_processed_60 = new Metric();
	private final Metric import_processed_60 = new Metric();

	private final Timer timer_60 = new Timer();

	public Statistics()
	{
		timer_60.Start();
	}

	public void SleepTime(long sleep_time)
	{
		sleep_60.Collect(sleep_time);
	}

	public void Request(int add, int queue)
	{
		request_processed_60.Collect(add);
		request_queue_60.Collect(queue);
	}

	public void BlockingRequest(int add, int queue)
	{
		blocking_request_processed_60.Collect(add);
		blocking_request_queue_60.Collect(queue);
	}

	public void Import(int add, int queue)
	{
		import_processed_60.Collect(add);
		import_queue_60.Collect(queue);
	}

	private static String GetAvgs(Metric metric, String name)
	{
		StringBuilder res = new StringBuilder();

		res.append(name).append(" avg: ").append(String.format("%.2f", metric.GetAvg())).append(System.lineSeparator());
		res.append(name).append(" min: ").append(metric.GetMin()).append(System.lineSeparator());
		res.append(name).append(" max: ").append(metric.GetMax()).append(System.lineSeparator());

		return res.toString();
	}

	private static String GetChange(Metric metric, int seconds, String name)
	{
		StringBuilder res = new StringBuilder();

		res.append(name).append(" for ").append(seconds).append(" secs: ")
			.append(metric.GetValue()).append(System.lineSeparator());

		if(seconds > 0)
			res.append(name).append(" per sec: ")
				.append(String.format("%.2f", 1.0 * metric.GetValue() / seconds))
					.append(System.lineSeparator());

		return res.toString();
	}

	public String GetStat()
	{
		String res = "";

		res += Statistics.GetAvgs(sleep_60, "sleep time");
		res += System.lineSeparator();

		res += Statistics.GetAvgs(request_queue_60, "request queue");
		res += System.lineSeparator();

		res += Statistics.GetAvgs(blocking_request_queue_60, "blocking request queue");
		res += System.lineSeparator();

		res += Statistics.GetAvgs(import_queue_60, "import queue");
		res += System.lineSeparator();

		res += Statistics.GetChange(request_processed_60, (int)timer_60.Get(), "requests");
		res += System.lineSeparator();

		res += Statistics.GetChange(blocking_request_processed_60, (int)timer_60.Get(), "blocking requests");
		res += System.lineSeparator();

		res += Statistics.GetChange(import_processed_60, (int)timer_60.Get(), "imported");
		res += System.lineSeparator();

		return res;
	}

	public void Process()
	{
		if(timer_60.Get() > 60) /*secs in min..*/
		{
			request_queue_60.Reset();
			blocking_request_queue_60.Reset();
			import_queue_60.Reset();

			request_processed_60.Reset();
			blocking_request_processed_60.Reset();
			import_processed_60.Reset();

			timer_60.Start();
		}
	}
}
