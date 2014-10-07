package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;

import java.util.HashMap;
import java.util.Map;

public class ModelCache
{
	public void Clean()
	{

	}

	private static class ObjectCache
	{
		private final String name;
		private final ModelObjectList cache;

		public ObjectCache(String name, ModelObjectList data)
		{
			this.name = name;
			cache = new ModelObjectList(data.Filter(name));
		}

		ModelObjectList Get()
		{
			return cache;
		}
	}

	private static class LinkCache
	{
		private final String name;
		private final ModelLinkList cache;

		public LinkCache(String name, ModelLinkList data)
		{
			this.name = name;
			cache = new ModelLinkList(data.Filter(name));
		}

		ModelLinkList Get()
		{
			return cache;
		}
	}

	Map<String, ObjectCache> object_cache;
	Map<String, LinkCache> link_cache;

	public ModelCache()
	{
		object_cache = new HashMap<>();
		link_cache = new HashMap<>();
	}

	public void EnableObjectIndex(String name, ModelObjectList data)
	{
		object_cache.put(name, new ObjectCache(name, data));
	}

	public void EnableLinkIndex(String name, ModelLinkList data)
	{
		link_cache.put(name, new LinkCache(name, data));
	}

	public ModelObjectList GetObjects(String name)
	{
		ObjectCache cache = object_cache.get(name);

		if(cache == null)
			return new ModelObjectList();

		return cache.Get();
	}

	public ModelLinkList GetLinks(String name)
	{
		LinkCache cache = link_cache.get(name);

		if(cache == null)
			return new ModelLinkList();

		return cache.Get();
	}
}
