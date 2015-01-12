/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.HashMap;
import java.util.Map;

public class ModelCache
{
	private static class ObjectValueCache
	{
		private final String cache_name;
		private final Map<Value, ModelObjectList> value_cache = new HashMap<>();
		private final ModelObjectList cache;

		public ObjectValueCache(String cache_name, ModelObjectList objects)
		{
			this.cache_name = cache_name;
			cache = objects.Filter(cache_name);

			for(ModelObject object : cache)
			{
				Value value = object.GetAttribute(cache_name).GetValue();

				ModelObjectList list = value_cache.get(value);

				if(list == null)
				{
					list = new ModelObjectList();
					value_cache.put(value, list);
				}

				list.add(object);
			}
		}

		public ModelObjectList Get(String name, Value value)
		{
			if(!cache_name.equals(name))
				throw new ExceptionModelFail("wrong cache: " + cache_name + " does not contain " + name);

			ModelObjectList result = value_cache.get(value);

			if(result == null)
				return new ModelObjectList();

			return result;
		}

		public ModelObjectList Get()
		{
			return cache;
		}
	}

	private static class LinkValueCache
	{
		private final String cache_name;
		private final Map<Value, ModelLinkList> value_cache = new HashMap<>();
		private final ModelLinkList cache;

		public LinkValueCache(String cache_name, ModelLinkList objects)
		{
			this.cache_name = cache_name;
			cache = objects.Filter(cache_name);

			for(ModelLink object : cache)
			{
				Value value = object.GetAttribute(cache_name).GetValue();

				ModelLinkList list = value_cache.get(value);

				if(list == null)
				{
					list = new ModelLinkList();
					value_cache.put(value, list);
				}

				list.add(object);
			}
		}

		public ModelLinkList Get(String name, Value value)
		{
			if(!cache_name.equals(name))
				throw new ExceptionModelFail("wrong cache: " + cache_name + " does not contain " + name);

			ModelLinkList result = value_cache.get(value);

			if(result == null)
				return new ModelLinkList();

			return result;
		}

		public ModelLinkList Get()
		{
			return cache;
		}
	}


	Map<String, ObjectValueCache> object_cache = new HashMap<>();
	Map<String, LinkValueCache> link_cache = new HashMap<>();

	public void EnableObjectIndex(String name, ModelObjectList data)
	{
		object_cache.put(name, new ObjectValueCache(name, data));
	}

	public void EnableLinkIndex(String name, ModelLinkList data)
	{
		link_cache.put(name, new LinkValueCache(name, data));
	}

	public ModelObjectList GetObjects(String name)
	{
		ObjectValueCache cache = object_cache.get(name);

		if(cache == null)
			return new ModelObjectList();

		return cache.Get();
	}

	public ModelLinkList GetLinks(String name)
	{
		LinkValueCache cache = link_cache.get(name);

		if(cache == null)
			return new ModelLinkList();

		return cache.Get();
	}

	public ModelLinkList GetLinks(String name, Value value)
	{
		LinkValueCache cache = link_cache.get(name);

		if(cache == null)
			return new ModelLinkList();

		return cache.Get(name, value);
	}

	public ModelObjectList GetObjects(String name, Value value)
	{
		ObjectValueCache cache = object_cache.get(name);

		if(cache == null)
			return new ModelObjectList();

		return cache.Get(name, value);
	}

	public void Clean()
	{
		object_cache = new HashMap<>();
		link_cache = new HashMap<>();
	}
}
