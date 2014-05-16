/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.utils;

public class DynamicSleeper
{
	private static final long MIN_SLEEP_TIME = 1;
	private static final long MAX_SLEEP_TIME = 128;
	private static final long GROWTH_RATE = 2;

	private long min_sleep_time = DynamicSleeper.MIN_SLEEP_TIME;
	private long max_sleep_time = DynamicSleeper.MAX_SLEEP_TIME;

	private long sleep_time;

	public DynamicSleeper(long min_sleep_time, long max_sleep_time)
	{
		this.min_sleep_time = min_sleep_time;
		this.max_sleep_time = max_sleep_time;

		this.sleep_time = min_sleep_time;
	}

	public DynamicSleeper()
	{
		this.sleep_time = min_sleep_time;
	}

	public void Act()
	{
		if(sleep_time > min_sleep_time)
			sleep_time /= DynamicSleeper.GROWTH_RATE;
	}

	public void Delay()
	{
		if(sleep_time < max_sleep_time)
			sleep_time *= DynamicSleeper.GROWTH_RATE;
	}

	public void Sleep()
		throws InterruptedException
	{
		if(sleep_time > min_sleep_time)
			Thread.sleep(sleep_time);
	}

	public long GetSleepTime()
	{
		return sleep_time;
	}

	public void Sleep(boolean more)
		throws InterruptedException
	{
		Thread.sleep(sleep_time);

		if(more)
			Delay();
		else
			Act();
	}
}
