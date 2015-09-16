/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JavaUtils
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public static long GetTimestamp()
	{
		return System.currentTimeMillis() / 1000L; /*milliseconds in second*/
	}

	public static String GetDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String Quotify(String str)
	{
		return "\"" + str + "\"";
	}

	private static final long EXECUTOR_TIMEOUT = 2;

	public static void ShutdownExecutor(ExecutorService executor)
	{
		executor.shutdown();

		boolean result = false;
		try
		{
			result = executor.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.SECONDS);
		}
		catch(InterruptedException ignore){}

		if(!result)
			LOGGER.log(Level.WARNING, "failed to stop executor: timeout");
	}
}
