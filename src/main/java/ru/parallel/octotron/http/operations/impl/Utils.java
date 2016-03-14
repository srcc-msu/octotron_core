/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.Const;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exception.ExceptionParseError;
import ru.parallel.octotron.reactions.PreparedResponseFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * implementation of all available http operations<br>
 * */
public abstract class Utils
{
	public static List<String> GetNames(String value)
	{
		List<String> names = new LinkedList<>();

		if(value != null)
			Collections.addAll(names, value.split(","));

		return names;
	}

	public static void RequiredParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		for(String name : names)
		{
			String value = params.get(name);

			if(value == null)
				throw new ExceptionParseError("missing a required param: " + name);
		}
	}

	public static void AllParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		Set<String> check_set = new HashSet<>(Arrays.asList(names));

		for(String name : params.keySet())
		{
			if(!check_set.contains(name))
				throw new ExceptionParseError("unexpected param: " + name);
		}
	}

	public static void StrictParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		Utils.RequiredParams(params, names);
		Utils.AllParams(params, names);
	}

	public static List<Attribute> GetAttributes(ModelEntity entity, List<String> names)
		throws ExceptionParseError
	{
		return GetAttributes(entity, names, null);
	}

	private static final Pattern PATTERN_NAME_PATH = Pattern.compile("([^:{}]+):([^:{}]+)");

	public static List<Attribute> GetAttributes(ModelEntity entity, List<String> names, String default_value_or_null)
		throws ExceptionParseError
	{
		List<Attribute> result = new LinkedList<>();

		for(String name : names)
		{
			Matcher matcher = PATTERN_NAME_PATH.matcher(name);

			if(matcher.find())
			{
				String path = matcher.group(1);
				String param_name = matcher.group(2);

				ModelList<? extends ModelEntity, ?> target = PreparedResponseFactory.GetRelativePath(path, entity);

				result.add(target.Only().GetAttribute(param_name));
			}
			else
			{
				if(entity.TestAttribute(name))
					result.add(entity.GetAttribute(name));
				else if(default_value_or_null != null)
					result.add(new Const(entity, "_", Value.ValueFromString(default_value_or_null)));
			}
		}

		return result;
	}

	public static List<List<Map<String, Object>>> GetAttributes(ModelList<? extends ModelEntity, ?> entities
		, List<String> names, boolean verbose)
		throws ExceptionParseError
	{
		List<List<Map<String, Object>>> data = new LinkedList<>();

		for(ModelEntity entity : entities)
		{
			List<Map<String, Object>> list = new LinkedList<>();

			for(Attribute attribute : GetAttributes(entity, names))
				list.add(attribute.GetRepresentation(verbose));

			data.add(list);
		}

		return data;
	}

	public static List<Object> GetAttributes(ModelList<? extends ModelEntity, ?> entities
		, boolean verbose, String type)
	{
		List<Object> data = new LinkedList<>();

		for(ModelEntity entity : entities)
		{
			data.add(entity.GetRepresentation(verbose).get(type));
		}

		return data;
	}

	public static List<Object> GetAttributes(ModelList<? extends ModelEntity, ?> entities
		,boolean verbose)
	{
		List<Object> data = new LinkedList<>();

		for(ModelEntity entity : entities)
		{
			data.add(entity.GetRepresentation(verbose));
		}

		return data;
	}

	public static String PrintCsvAttributes(ModelList<? extends ModelEntity, ?> entities
		, List<String> names)
		throws ExceptionParseError
	{
		StringBuilder result = new StringBuilder();
		String sep = ",";

		String prefix = "";
		for(String name : names)
		{
			result.append(prefix).append(name);
			prefix = sep;
		}
		result.append(System.lineSeparator());

		for(ModelEntity entity : entities)
		{
			prefix = "";

			for(Attribute attribute : GetAttributes(entity, names, "<not found>"))
			{
				result.append(prefix).append(attribute.ValueToString());
				prefix = sep;
			}

			result.append(System.lineSeparator());
		}

		return result.toString();
	}
}
