/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.parallel.utils.JavaUtils.SortSimpleList;

public class AutoFormat
{
	public enum E_FORMAT_PARAM {CSV, COMMA, NL, JSON, JSONP, NONE}

	/**
	 * print \\list objects with custom separators
	 * if \\attributes list is empty - print all attributes
	 * */
	public static String PrintSeparated(IEntityList<?> list, List<SimpleAttribute> attributes
		, String attr_sep, String line_sep, boolean show_name, boolean separate_objects)
	{
		StringBuilder result = new StringBuilder();

		for(IEntity entity : list)
		{
			List<SimpleAttribute> target;

			if(attributes.size() > 0)
				target = SortSimpleList(attributes);
			else
				target = entity.GetAttributes().AlphabeticSort().ToSimple();

			String prefix = "";

			for(SimpleAttribute attr : target)
			{
				if(entity.TestAttribute(attr.GetName()))
				{
					result.append(prefix);

					if(show_name)
					{
						result.append(attr.GetName());
						result.append('=');
					}

					Object value = entity.GetAttribute(attr.GetName()).GetValue();
					result.append(SimpleAttribute.ValueToStr(value));
				}
				else
					result.append("not found");

				prefix = attr_sep;
			}
			result.append(line_sep);

			if(separate_objects)
				result.append(line_sep);
		}

		return result.toString();
	}

	/**
	 * print \\list objects in json format
	 * example: [{"attr_1"=0, "attr2"="test"},{"attr_name"=1.0}]
	 * if \\attributes list is empty - print all attributes
	 * */
	public static String PrintJson(IEntityList<?> list, List<SimpleAttribute> attributes)
	{
		StringBuilder result = new StringBuilder();

		String ent_prefix = "";

		result.append('[');

		for(IEntity entity : list)
		{
			result.append(ent_prefix);
			result.append('{');

			String prefix = "";

			List<SimpleAttribute> target;

			if(attributes.size() > 0)
				target = SortSimpleList(attributes);
			else
				target = entity.GetAttributes().AlphabeticSort().ToSimple();

			for(SimpleAttribute attr : target)
			{
				result.append(prefix);
				if(entity.TestAttribute(attr.GetName()))
				{
					result.append(JavaUtils.Quotify(attr.GetName()));
					result.append(':');

					Object value = entity.GetAttribute(attr.GetName()).GetValue();
					result.append(SimpleAttribute.ValueToStr(value));
				}
				else
					result.append("\"not found\":null");

				prefix = ",";
			}
			result.append('}');
			ent_prefix = ",";
		}
		result.append(']');

		return result.toString();
	}

	public static String PrintJsonP(IEntityList<?> list, List<SimpleAttribute> attributes, String callback)
	{
		String result = callback + "({" + System.lineSeparator();
		result += "\"modified\" : " + JavaUtils.GetTimestamp() + "," + System.lineSeparator();
		result += "\"data\" : " + PrintJson(list, attributes);
		result += System.lineSeparator() + "})";

		return result;
	}

	public static String PrintCSV(IEntityList<?> list, List<SimpleAttribute> attributes)
	{
		StringBuilder result = new StringBuilder();

		for(SimpleAttribute param : attributes)
			result.append(param.GetName()).append(",");

		result.append(System.lineSeparator());
		result.append(PrintSeparated(list, attributes, ",", System.lineSeparator(), false, false));

		return result.toString();
	}

	public static String PrintNL(IEntityList<?> list, List<SimpleAttribute> attributes)
	{
		return PrintSeparated(list, attributes, System.lineSeparator(), System.lineSeparator(), true, true);
	}

	public static String PrintComma(IEntityList<?> list, List<SimpleAttribute> attributes)
	{
		return PrintSeparated(list, attributes, ",", ",", true, false);
	}

	public static String PrintEntities(IEntityList<?> list, List<SimpleAttribute> attributes, E_FORMAT_PARAM format, String callback)
	{
		switch (format)
		{
			case CSV:
				return PrintCSV(list, attributes);

			case COMMA:
				return PrintComma(list, attributes);

			case NL:
				return PrintNL(list, attributes);

			case JSON:
				return PrintJson(list, attributes);

			case JSONP:
				return PrintJsonP(list, attributes, callback);

			default:
				throw new IllegalArgumentException("unsupported format " + format);
		}
	}

	public static String PrintEntitiesSpecial(IEntityList<? extends ModelEntity> list)
	{
		StringBuilder result = new StringBuilder();

		for(ModelEntity entity : list)
		{
			result.append(System.lineSeparator()).append("----  attributes   ----")
				.append(System.lineSeparator()).append(System.lineSeparator());

			for(IAttribute attr : entity.GetAttributes().AlphabeticSort())
			{
				result.append(attr.GetName()).append('=');
				Object value = entity.GetAttribute(attr.GetName()).GetValue();
				result.append(SimpleAttribute.ValueToStr(value));

				result.append(System.lineSeparator());
			}

			result.append(System.lineSeparator()).append("----  rules  ----")
				.append(System.lineSeparator()).append(System.lineSeparator());

/*			for(OctoRule rule : entity.GetRules())
			{
				String str = " ID: " + rule.GetID() + " attribute: \""
					+ rule.GetName() + "\"" + System.lineSeparator();

				result.append(str);
			}*/

			result.append(System.lineSeparator()).append("----  reactions  ----")
				.append(System.lineSeparator()).append(System.lineSeparator());

			for(OctoReaction reaction : entity.GetReactions())
			{
				String str = " ID: " + reaction.GetID()
					+ " descr: \"" + reaction.GetResponse().GetDescription() + "\""
					+ System.lineSeparator();

				result.append(str);
			}

			result.append(System.lineSeparator()).append("----  markers  ----")
					.append(System.lineSeparator()).append(System.lineSeparator());

			for(Marker marker : entity.GetMarkers())
			{
				String str = " reaction: " + marker.GetTarget()
					+ " descr: \"" + marker.GetDescription() + "\""
					+ " suppressed = " + marker. IsSuppress()
					+ System.lineSeparator();
				result.append(str);
			}
		}

		return result.toString();
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
