/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import ru.parallel.octotron.core.IPresentable;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.*;

import static ru.parallel.utils.JavaUtils.Quotify;

public class AutoFormat
{
	public enum E_FORMAT_PARAM { PLAIN, JSON, JSONP, NONE}

	public static String PrintData(Collection<Map<String, Object>> data, E_FORMAT_PARAM format, String callback)
	{
		switch (format)
		{
			case PLAIN:
				return PrintNL(data);

			case JSON:
				return PrintJson(data);

			case JSONP:
				return PrintJsonp(data, callback);

			default:
				throw new IllegalArgumentException("unsupported format " + format);
		}
	}

	public static String PrintJsonp(Collection<Map<String, Object>> data, String callback)
	{
		StringBuilder result = new StringBuilder();

		result.append(callback).append("({")
			.append(System.lineSeparator());

		result.append(Quotify("modified")).append(':')
			.append(JavaUtils.GetTimestamp()).append(',')
			.append(System.lineSeparator());

		result.append(Quotify("data")).append(':')
			.append(System.lineSeparator());

		result.append(PrintJson(data))
			.append(System.lineSeparator());

		result.append("})");

		return result.toString();
	}

	public static String PrintJson(Map<String, Object> data)
	{
		StringBuilder result = new StringBuilder();

		List<String> names = new ArrayList<>(data.keySet());
		Collections.sort(names);

		result.append("{");

		String inner_prefix = "";
		for(String name : names)
		{
			String string_value = PrintJson(data.get(name));

			result.append(inner_prefix)
				.append(Quotify(name))
				.append(':')
				.append(string_value);

			inner_prefix = ",";
		}

		result.append('}');

		return result.toString();
	}

	public static String PrintJson(Collection<Object> data)
	{
		StringBuilder result = new StringBuilder();

		result.append('[');

		String prefix = "";

		for(Object string : data)
		{
			result.append(prefix).append(PrintJson(string));
			prefix = ",";
		}

		result.append(']');

		return result.toString();
	}

	public static String PrintJson(Object data)
	{
		if(data instanceof IPresentable)
			return PrintJson(((IPresentable)data).GetRepresentation());
		if(data instanceof Map)
			return PrintJson((Map<String, Object>) data);
		if(data instanceof Collection)
			return PrintJson((Collection<Object>) data);
		if(data instanceof EEventStatus)
			return PrintJson(data.toString());
		else
			return SimpleAttribute.ValueToStr(data);
	}

/*
	public static String PrintJson(Collection<Map<String, Object>> data)
	{
		List<String> strings = new LinkedList<>();

		for(Map<String, Object> dict : data)
			strings.add(PrintJson(dict));

		return PrintJson(strings);
	}*/

	public static String PrintNL(Collection<Map<String, Object>> data)
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
	}
}
