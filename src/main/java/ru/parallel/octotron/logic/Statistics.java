/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.utils.Timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * simplifies statistics collection
 * add is thread safe, read functions are not
 * */
public class Statistics
{
	private final List<BGExecutorWrapper> registered_services = new CopyOnWriteArrayList<>();

    public static final class Metric
    {
        private Long total;
        private Long sum;
        private Long min_value;
        private Long max_value;
        private Long current;

        private int count;

        public Metric()
        {
            total = 0L;
            Reset();
        }

        public void Reset()
        {
            sum = null;
            min_value = null;
            max_value = null;

            count = 0;
        }

        public void Collect(long value)
        {
            current = value;

            if(total == null)
                total = value;
            else
                total += value;

            if(sum == null)
                sum = value;
            else
                sum += value;

            if(min_value == null || min_value > value)
                min_value = value;

            if(max_value == null || max_value < value)
                max_value = value;

            count++;
        }

        public double GetAvg()
        {
            if(sum == null || count == 0)
                return 0.0;

            return 1.0 * sum / count;
        }

        public long GetMin()
        {
            if(min_value == null)
                return 0;

            return min_value;
        }

        public long GetMax()
        {
            if(max_value == null)
                return 0;

            return max_value;
        }

        public long GetSum()
        {
            if(sum == null)
                return 0;

            return sum;
        }

        public long GetTotal()
        {
            return total;
        }

        public long GetCurrent()
        {
            return current;
        }
    }

	public static class Stat
	{
		public final String name;

		public final Metric queue_size_metric = new Metric();
		public final Metric total_metric = new Metric();

		public Stat(String name)
		{
			this.name = name;
		}
	}

	final Map<String, Stat> stats = new HashMap<>();

	private final Timer timer_60 = new Timer();

	public void RegisterService(BGExecutorWrapper service)
	{
		registered_services.add(service);
	}

	public Statistics()
	{
		timer_60.Start();
	}

	private void Add(String name, long added_count, long queue_size)
	{
		Stat stat = stats.get(name);

		if(stat == null)
		{
			stat = new Stat(name);
			stats.put(name, stat);
		}

		stat.total_metric.Collect(added_count);
		stat.queue_size_metric.Collect(queue_size);
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

		res.put(name + " total"
			, String.valueOf(metric.GetTotal()));

		res.put(name + " for " + seconds + " secs"
			, String.valueOf(metric.GetSum()));

		if(seconds > 0)
			res.put(name + " per sec"
				, String.format("%.2f", 1.0 * metric.GetSum() / seconds));

		return res;
	}

	public List<Map<String, Object>> GetRepresentation()
	{
		List<Map<String, Object>> res = new LinkedList<>();

		for(Stat stat : stats.values())
		{
			Map<String, Object> to_add = Statistics.GetAvgs(stat.queue_size_metric, stat.name + " queue");

			to_add.putAll(Statistics.GetChange(stat.total_metric, (int) timer_60.Get(), stat.name));

			res.add(to_add);
		}

		return res;
	}

	public void Process()
	{
		for(BGExecutorWrapper service : registered_services)
			Add(service.GetName(), service.GetRecentCompletedCount(), service.GetWaitingCount());

		if(timer_60.Get() > 60) /*secs in min..*/
		{
			for(Stat stat : stats.values())
			{
				stat.queue_size_metric.Reset();
				stat.total_metric.Reset();
			}

			timer_60.Start();
		}
	}
}
