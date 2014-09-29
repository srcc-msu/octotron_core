/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.collections;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.ReactionObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.utils.JavaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.parallel.utils.JavaUtils.SortSimpleList;

public class AutoFormat
{
	public enum E_FORMAT_PARAM { PLAIN, JSON, JSONP, NONE}


	public static String PrintData(List<Map<String, Object>> data, E_FORMAT_PARAM format, String callback)
	{
		switch (format)
		{
			case PLAIN:
				return PrintPlain(data);

			case JSON:
				return PrintJson(data);

			case JSONP:
				return PrintJsonp(data, callback);

			default:
				throw new IllegalArgumentException("unsupported format " + format);
		}
	}

	public static String PrintJsonp(List<Map<String, Object>> data, String callback)
	{
		StringBuilder result = new StringBuilder();

		result.append(callback).append("({")
			.append(System.lineSeparator());

		result.append(SimpleAttribute.ValueToStr("modified")).append(':')
			.append(JavaUtils.GetTimestamp()).append(',')
			.append(System.lineSeparator());

		result.append(SimpleAttribute.ValueToStr("data")).append(':')
			.append(System.lineSeparator());

		result.append(PrintJson(data))
			.append(System.lineSeparator());

		result.append("})");

		return result.toString();
	}

	public static String PrintJson(List<Map<String, Object>> data)
	{
		StringBuilder result = new StringBuilder();

		result.append('[');

		String prefix = "";

		for(Map<String, Object> dict : data)
		{
			List<String> names = new ArrayList<>(dict.keySet());
			Collections.sort(names);

			result.append(prefix).append("{");

			String inner_prefix = "";
			for(String name : names)
			{
				result.append(inner_prefix)
					.append(SimpleAttribute.ValueToStr(name))
					.append(':')
					.append(SimpleAttribute.ValueToStr(dict.get(name)));

				inner_prefix = ",";
			}

			result.append('}');
			prefix = ",";
		}

		result.append(']');
		return result.toString();
	}

	public static String PrintPlain(List<Map<String, Object>> data)
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
					.append(name)
					.append(" = ")
					.append(SimpleAttribute.ValueToStr(map.get(name)));

				inner_prefix = ", ";
			}

			prefix = System.lineSeparator();
		}

		return result.toString();
	}
}
