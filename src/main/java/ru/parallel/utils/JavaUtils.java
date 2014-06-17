/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaUtils
{
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
