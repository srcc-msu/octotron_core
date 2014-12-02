/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import com.google.gson.*;
import ru.parallel.utils.format.JsonString;

public abstract class JsonUtils
{
	public static boolean IsPresent(JsonObject base, String name)
	{
		return base.get(name) != null;
	}

	public static JsonElement MustPresent(JsonObject base, String name)
	{
		if(base.get(name) == null)
			throw new RuntimeException("wrong config file, missing: " + name);
		return base.get(name);
	}

	public static JsonString Prettify(JsonString json_string)
	{
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(json_string.string);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		return new JsonString(gson.toJson(json));
	}
}
