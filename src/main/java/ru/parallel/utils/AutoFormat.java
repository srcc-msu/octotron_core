/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.utils.format.JsonString;
import ru.parallel.utils.format.JsonpString;
import ru.parallel.utils.format.TypedString;

import java.util.*;

import static ru.parallel.utils.JavaUtils.Quotify;

public class AutoFormat
{
	public static JsonpString ToJsonp(TypedString data, String callback)
	{
		return new JsonpString(FormatJsonp(data, callback));
	}

	public static JsonString PrintJson(Object data)
	{
		return new JsonString(FormatJson(data));
	}

	public static String FormatJson(Object data)
	{
		if(data == null)
			return JavaUtils.Quotify("null");

		if(data instanceof JsonString)
			return ((JsonString) data).string;
		if(data instanceof Map)
			return FormatJson((Map<String, Object>) data);
		if(data instanceof Collection)
			return FormatJson((Collection<Object>) data);
		if(data instanceof Value)
			return ((Value)data).ValueToString();
		else
			return Value.Construct(data).ValueToString();
	}

	public static String FormatJsonp(Object data, String callback)
	{
		StringBuilder result = new StringBuilder();

		result.append(callback).append("({")
			.append(System.lineSeparator());

		result.append(Quotify("modified")).append(':')
			.append(JavaUtils.GetTimestamp()).append(',')
			.append(System.lineSeparator());

		result.append(Quotify("data")).append(':')
			.append(System.lineSeparator());

		result.append(FormatJson(data))
			.append(System.lineSeparator());

		result.append("})");

		return result.toString();
	}

	public static String FormatJson(Map<String, Object> data)
	{
		StringBuilder result = new StringBuilder();

		List<String> names = new ArrayList<>(data.keySet());
		Collections.sort(names);

		result.append("{");

		String inner_prefix = "";
		for(String name : names)
		{
			String string_value = FormatJson(data.get(name));

			result.append(inner_prefix)
				.append(Quotify(name))
				.append(':')
				.append(string_value);

			inner_prefix = ",";
		}

		result.append('}');

		return result.toString();
	}

	public static String FormatJson(Collection<Object> data)
	{
		StringBuilder result = new StringBuilder();

		result.append('[');

		String prefix = "";

		for(Object string : data)
		{
			result.append(prefix).append(FormatJson(string));
			prefix = ",";
		}

		result.append(']');

		return result.toString();
	}

/*	public static String PrintNL(Collection<Map<String, Object>> data)
	{
		StringBuilder result = new StringBuilder();

		for(Map<String, Object> map : data)
		{
			List<String> names = new ArrayList<>(map.keySet());
			Collections.sort(names);

			for(String name : names)
			{
				result.append(Quotify(name))
					.append(" = ")
					.append(SimpleAttribute.ValueToStr(map.get(name)))
					.append(System.lineSeparator());
			}

			result.append(System.lineSeparator());
		}

		return result.toString();
	}

	public static String PrintPlain(Collection<Map<String, Object>> data)
	{
		StringBuilder result = new StringBuilder();

		String prefix = "";

		for(Map<String, Object> map : data)
		{
			List<String> names = new ArrayList<>(map.keySet());
			Collections.sort(names);

			result.append(prefix);

			String inner_prefix = "";
			for(String name : names)
			{
				result.append(inner_prefix)
					.append(Quotify(name))
					.append(" = ")
					.append(SimpleAttribute.ValueToStr(map.get(name)));

				inner_prefix = ", ";
			}

			prefix = System.lineSeparator();
		}

		return result.toString();
	}*/
}
