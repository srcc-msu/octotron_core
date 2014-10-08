package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.HashMap;
import java.util.Map;

public class ModelCache
{
	private static class ObjectValueCache
	{
		private final String name;
		private final Map<Object, ModelObjectList> value_cache = new HashMap<>();
		private final ModelObjectList cache;

		public ObjectValueCache(String name, ModelObjectList objects)
		{
			this.name = name;
			cache = objects.Filter(name);

			for(ModelObject object : cache)
			{
				Object value = object.GetAttribute(name).GetValue();

				ModelObjectList list = value_cache.get(value);

				if(list == null)
				{
					list = new ModelObjectList();
					value_cache.put(value, list);
				}

				list.add(object);
			}
		}

		public ModelObjectList Get(SimpleAttribute attribute)
		{
			if(!name.equals(attribute.GetName()))
				throw new ExceptionModelFail("wrong cache: " + name + " does not contain " + attribute.GetName());

			ModelObjectList result = value_cache.get(attribute.GetValue());

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
		private final String name;
		private final Map<Object, ModelLinkList> value_cache = new HashMap<>();
		private final ModelLinkList cache;

		public LinkValueCache(String name, ModelLinkList objects)
		{
			this.name = name;
			cache = objects.Filter(name);

			for(ModelLink object : cache)
			{
				Object value = object.GetAttribute(name).GetValue();

				ModelLinkList list = value_cache.get(value);

				if(list == null)
				{
					list = new ModelLinkList();
					value_cache.put(value, list);
				}

				list.add(object);
			}
		}

		public ModelLinkList Get(SimpleAttribute attribute)
		{
			if(!name.equals(attribute.GetName()))
				throw new ExceptionModelFail("wrong cache: " + name + " does not contain " + attribute.GetName());

			ModelLinkList result = value_cache.get(attribute.GetValue());

			if(result == null)
				return new ModelLinkList();

			return result;
		}

		public ModelLinkList Get()
		{
			return cache;
		}
	}


	Map<String, ObjectValueCache> object_cache;
	Map<String, LinkValueCache> link_cache;

	public ModelCache()
	{
		object_cache = new HashMap<>();
		link_cache = new HashMap<>();
	}

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

	public ModelLinkList GetLinks(SimpleAttribute attribute)
	{
		LinkValueCache cache = link_cache.get(attribute.GetName());

		if(cache == null)
			return new ModelLinkList();

		return cache.Get(attribute);
	}

	public ModelObjectList GetObjects(SimpleAttribute attribute)
	{
		ObjectValueCache cache = object_cache.get(attribute.GetName());

		if(cache == null)
			return new ModelObjectList();

		return cache.Get(attribute);
	}

	public void Clean()
	{
		object_cache = new HashMap<>();
		link_cache = new HashMap<>();
	}
}
