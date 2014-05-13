/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils
{
	public static JsonElement MustPresent(JsonObject base, String name)
	{
		if(base.get(name) == null)
			throw new RuntimeException("wrong config file, missing: " + name);
		return base.get(name);
	}
}
