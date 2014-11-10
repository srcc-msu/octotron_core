/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.util.*;

/**
 * implementation of all available http operations<br>
 * */
public abstract class Utils
{
	public static List<SimpleAttribute> GetAttributes(String names)
	{
		List<SimpleAttribute> attributes = new LinkedList<>();

		if(names != null)
			for(String name : names.split(","))
				attributes.add(new SimpleAttribute(name, null));

		return attributes;
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

	public static List<IModelAttribute> GetAttributes(ModelEntity entity, List<SimpleAttribute> attributes)
	{
		List<IModelAttribute> result = new LinkedList<>();

		for(SimpleAttribute names : attributes)
		{
			result.add(entity.GetAttribute(names.GetName()));
		}

		return result;
	}

	public static List<List<Map<String, Object>>> GetAttributes(ModelList<? extends ModelEntity, ?> entities
		, List<SimpleAttribute> attributes, boolean verbose)
	{
		List<List<Map<String, Object>>> data = new LinkedList<>();

		for(ModelEntity entity : entities)
		{
			List<Map<String, Object>> list = new LinkedList<>();

			for(IModelAttribute attribute : GetAttributes(entity, attributes))
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
		, List<SimpleAttribute> attributes)
	{
		StringBuilder result = new StringBuilder();
		String sep = ",";

		String prefix = "";
		for(SimpleAttribute attribute : attributes)
		{
			result.append(prefix).append(attribute.GetName());
			prefix = sep;
		}
		result.append(System.lineSeparator());

		for(ModelEntity entity : entities)
		{
			prefix = "";
			for(SimpleAttribute attribute : attributes)
			{

				String string_value;

				if(entity.TestAttribute(attribute.GetName()))
					string_value = entity.GetAttribute(attribute.GetName()).GetStringValue();
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
