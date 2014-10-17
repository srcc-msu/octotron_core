/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.Collection;

public final class ModelData
{
	final ModelCache cache;

	final ModelObjectList objects;
	final ModelLinkList links;

	public ModelData()
	{
		cache = new ModelCache();

		objects = new ModelObjectList();
		links = new ModelLinkList();
	}

	public ModelObjectList GetAllObjects()
	{
		return objects;
	}

	public ModelLinkList GetAllLinks()
	{
		return links;
	}

	public ModelObjectList GetObjects(SimpleAttribute attribute)
	{
		return cache.GetObjects(attribute);
	}

	public ModelObjectList GetObjects(String name)
	{
		return cache.GetObjects(name);
	}

	public ModelLinkList GetLinks(SimpleAttribute attribute)
	{
		return cache.GetLinks(attribute);
	}

	public ModelLinkList GetLinks(String name)
	{
		return cache.GetLinks(name);
	}

	public Collection<ModelEntity> GetAllEntities()
	{
		return Lists.newArrayList(Iterators.concat(GetAllLinks().iterator(), GetAllObjects().iterator()));
	}
}
