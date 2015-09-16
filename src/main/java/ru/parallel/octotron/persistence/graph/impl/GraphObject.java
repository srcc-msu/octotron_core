/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph.impl;

import ru.parallel.octotron.core.primitive.Info;
import ru.parallel.octotron.persistence.graph.EGraphType;
import ru.parallel.octotron.persistence.graph.IGraph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * implementation of object according to real \graph<br>
 * */
public final class GraphObject extends GraphEntity
{
	public GraphObject(IGraph graph, Info<EGraphType> info)
	{
		super(graph, info);
	}

	public GraphObject(IGraph graph, long id)
	{
		super(graph, id, EGraphType.OBJECT);
	}

	@Override
	public Object GetAttribute(String name)
	{
		return graph.GetObjectAttribute(info, name);
	}

	public void DeleteAttribute(String name)
	{
		graph.DeleteObjectAttribute(info, name);
	}

	@Override
	public void UpdateAttribute(String name, Object value)
	{
		graph.SetObjectAttribute(info, name, value);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestObjectAttribute(info, name);
	}

	@Override
	public void Delete()
	{
		graph.DeleteObject(info);
	}

	@Override
	public List<String> GetAttributes()
	{
		return graph.GetObjectAttributes(info);
	}

//--------

	public Collection<GraphLink> GetInLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetInLinks(info));
	}

	public Collection<GraphLink> GetOutLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetOutLinks(info));
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

//--------

	public boolean TestLabel(String label)
	{
		return graph.TestNodeLabel(info, label);
	}

	public void AddLabel(String label)
	{
		graph.AddNodeLabel(info, label);
	}
}
