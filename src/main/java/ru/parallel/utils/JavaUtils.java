/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String Quotify(String str)
	{
		return "\"" + str + "\"";
	}


	public static <T extends SimpleAttribute> List<T> SortSimpleList(List<T> list)
	{
		List<T> new_list = new LinkedList<>(list);

		Collections.sort(new_list, new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				return o1.GetName().compareTo(o2.GetName());
			}
		});

		return new_list;
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

		if(result == false)
			LOGGER.log(Level.WARNING, "failed to stop executor: timeout");
	}
}
