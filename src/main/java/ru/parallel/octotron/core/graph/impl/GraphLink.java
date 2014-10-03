/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.EGraphType;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.primitive.UniqueID;

/**
 * implementation of link, that resides in \graph<br>
 * */
public final class GraphLink extends GraphEntity
{
	public GraphLink(IGraph graph, long id)
	{
		super(graph, id, EGraphType.LINK);
	}

	public GraphLink(IGraph graph, UniqueID<EGraphType> id)
	{
		super(graph, id);
	}

	@Override
	public Object GetAttribute(String name)
	{
		return graph.GetLinkAttribute(id, name);
	}

	@Override
	public void UpdateAttribute(String name, Object value)
	{
		graph.SetLinkAttribute(id, name, value);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestLinkAttribute(id, name);
	}

	@Override
	public void Delete()
	{
		graph.DeleteLink(id);
	}

	public GraphObject Target()
	{
		return new GraphObject(graph, graph.GetLinkTarget(id));
	}

	public GraphObject Source()
	{
		return new GraphObject(graph, graph.GetLinkSource(id));
	}

	public void DeleteAttribute(String name)
	{
		graph.DeleteLinkAttribute(id, name);
	}
}
