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

public abstract class JavaUtils
{
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

	public static void ShutdownExecutor(ExecutorService executor)
	{
		executor.shutdown();
		while(!executor.isShutdown())
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException ignore){}
		}
	}
}
