/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.IAttribute;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.util.*;

/**
 * implementation of all available http operations<br>
 * */
public abstract class Operations
{


	public static List<SimpleAttribute> GetAttributes(Map<String, String> params)
	{

		String attributes_str = params.get("attributes");
		List<SimpleAttribute> attributes = new LinkedList<>();

		if(attributes_str != null)
			for(String name : attributes_str.split(","))
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
		Operations.RequiredParams(params, names);
		Operations.AllParams(params, names);
	}

	public static List<IModelAttribute> GetAttributes(ModelEntity entity, List<SimpleAttribute> attributes, EAttributeType type)
	{
		List<IModelAttribute> result = new LinkedList<>();

		if(attributes.size() > 0)
		{
			for(SimpleAttribute names : attributes)
			{
				IModelAttribute attribute = entity.GetAttribute(names.GetName());

				if(type == null || attribute.GetType() == type)
					result.add(attribute);
			}
			return result;
		}
		else
		{
			for(IAttribute names : entity.GetAttributes())
			{
				IModelAttribute attribute = entity.GetAttribute(names.GetName());

				if(type == null || attribute.GetType() == type)
					result.add(attribute);
			}
			return result;
		}
	}

	public static Object GetAttributes(ModelList<? extends ModelEntity, ?> entities
		, List<SimpleAttribute> attributes, EAttributeType type)
	{
		if(attributes.size() > 0)
		{
			List<List<Map<String, Object>>> data = new LinkedList<>();

			for (ModelEntity entity : entities)
			{
				List<Map<String, Object>> list = new LinkedList<>();

				for (IModelAttribute attribute : GetAttributes(entity, attributes, type))
					list.add(attribute.GetShortRepresentation());

				data.add(list);
			}

			return data;
		}
		else
		{
			List<Map<String, Object>> data = new LinkedList<>();

			for (ModelEntity entity : entities)
			{
				data.add(entity.GetShortRepresentation());
			}
			return data;
		}
	}
}
