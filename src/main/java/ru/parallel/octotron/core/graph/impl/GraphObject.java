/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.EGraphType;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.primitive.UniqueID;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * implementation of object according to real \graph<br>
 * */
public final class GraphObject extends GraphEntity
{
	public GraphObject(IGraph graph, UniqueID<EGraphType> id)
	{
		super(graph, id);
	}

	public GraphObject(IGraph graph, long id)
	{
		super(graph, id, EGraphType.OBJECT);
	}

	@Override
	public Object GetAttribute(String name)
	{
		return graph.GetObjectAttribute(id, name);
	}

	public void DeleteAttribute(String name)
	{
		graph.DeleteObjectAttribute(id, name);
	}

	@Override
	public void UpdateAttribute(String name, Object value)
	{
		graph.SetObjectAttribute(id, name, value);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestObjectAttribute(id, name);
	}

	@Override
	public void Delete()
	{
		graph.DeleteObject(id);
	}

// ----
//
// ----

	public Collection<GraphLink> GetInLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetInLinks(id));
	}

	public Collection<GraphLink> GetOutLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetOutLinks(id));
	}

	public Collection<GraphObject> GetInNeighbors()
	{
		List<GraphObject> objects = new LinkedList<>();

		for(GraphLink link : GetInLinks())
			objects.add(link.Source());

		return objects;
	}

	public Collection<GraphObject> GetOutNeighbors()
	{
		List<GraphObject> objects = new LinkedList<>();

		for(GraphLink link : GetOutLinks())
			objects.add(link.Target());

		return objects;
	}
}
