/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.util.*;

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

	public static List<IModelAttribute> GetAttributes(ModelEntity entity, List<String> names)
	{
		List<IModelAttribute> result = new LinkedList<>();

		for(String name : names)
		{
			result.add(entity.GetAttribute(name));
		}

		return result;
	}

	public static List<List<Map<String, Object>>> GetAttributes(ModelList<? extends ModelEntity, ?> entities
		, List<String> names, boolean verbose)
	{
		List<List<Map<String, Object>>> data = new LinkedList<>();

		for(ModelEntity entity : entities)
		{
			List<Map<String, Object>> list = new LinkedList<>();

			for(IModelAttribute attribute : GetAttributes(entity, names))
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
			for(String name : names)
			{
				String string_value;

				if(entity.TestAttribute(name))
					string_value = entity.GetAttribute(name).GetStringValue();
				else
					string_value = "<not found>";

				result.append(prefix).append(string_value);
				prefix = sep;
			}
			result.append(System.lineSeparator());
		}

		return result.toString();
	}
}
