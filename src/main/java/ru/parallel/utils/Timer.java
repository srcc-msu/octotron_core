/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.utils;

public final class Timer
{
	private final static double NANOSECS = 1e9;
	private final static int DEF_PRES = 6;
	private static double static_start;

	private double start;

	public static void SStart()
	{
		Timer.static_start = System.nanoTime();
	}

	public static double SGet()
	{
		return (System.nanoTime() - Timer.static_start) / NANOSECS;
	}

	public static double SPrint(String s)
	{
		return SPrint(s, DEF_PRES);
	}

	public static double SPrint(String s, int pres)
	{
		double end = System.nanoTime();

		String format = "%s finished, took %." + pres + "f secs" + System.lineSeparator();

		System.out.printf(format, s, (end - Timer.static_start) / NANOSECS);

		return (end - Timer.static_start) / NANOSECS;
	}

	public static double SEnd()
	{
		return (System.nanoTime() - Timer.static_start) / NANOSECS;
	}

	public Timer()
	{
		Start();
	}

	public void Start()
	{
		start = System.nanoTime();
	}

	public double Get()
	{
		return (System.nanoTime() - start) / NANOSECS;
	}

	public double Print(String s)
	{
		double end = System.nanoTime();

		System.out.println(s + " finished, took " + (end - start)
			/ NANOSECS + " secs");

		return (end - start) / NANOSECS;
	}

}
