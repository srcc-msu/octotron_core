/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

public final class Metric
{
	private Long sum;
	private Long min_value;
	private Long max_value;
	private Long current;

	private int count;

	public Metric()
	{
		Reset();
	}

	private final Object lock = new Object();

	public void Reset()
	{
		synchronized(lock)
		{
			sum = null;
			min_value = null;
			max_value = null;

			count = 0;
		}
	}

	public void Collect(long value)
	{
		synchronized(lock)
		{
			current = value;

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

	public long GetValue()
	{
		if(sum == null)
			return 0;

		return sum;
	}

	public long GetCurrent()
	{
		return current;
	}
}
