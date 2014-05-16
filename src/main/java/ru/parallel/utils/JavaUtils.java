/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class JavaUtils
{
	public static Object[] ExtendMultiArray(Object[] a)
	{
		if(a == null)
			return new Object[0];

		int len = 0;

		for(Object o : a)
		{
			if(o instanceof Object[])
				len += ((Object[])o).length;
			else
				len++;
		}

		Object[] result = new Object[len];

		int cur = 0;

		for(Object o : a)
		{
			if(o instanceof Object[])
			{
				int l = ((Object[])o).length;

				if(l == 0)
					continue;

				System.arraycopy(o, 0, result, cur, l);
				cur += l;
			}
			else
			{
				result[cur] = o;
				cur++;
			}
		}

		return result;
	}

	public static <T> T[] ExtendMultiArrayChecked(Object[] a, Class<T[]> check)
	{
		Object[] plain = JavaUtils.ExtendMultiArray(a);

		return Arrays.copyOf(plain, plain.length, check);
	}

	public static long GetTimestamp()
	{
		return System.currentTimeMillis() / 1000L; /*miliseconds in second*/
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
}
