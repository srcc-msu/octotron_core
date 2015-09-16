/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph.impl;

import ru.parallel.octotron.core.primitive.Info;
import ru.parallel.octotron.persistence.graph.EGraphType;
import ru.parallel.octotron.persistence.graph.IGraph;

import java.util.List;

/**
 * implementation of link, that resides in \graph<br>
 * */
public final class GraphLink extends GraphEntity
{
	public GraphLink(IGraph graph, long id)
	{
		super(graph, id, EGraphType.LINK);
	}

	public GraphLink(IGraph graph, Info<EGraphType> info)
	{
		super(graph, info);
	}

	@Override
	public Object GetAttribute(String name)
	{
		return graph.GetLinkAttribute(info, name);
	}

	@Override
	public void UpdateAttribute(String name, Object value)
	{
		graph.SetLinkAttribute(info, name, value);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestLinkAttribute(info, name);
	}

	@Override
	public List<String> GetAttributes()
	{
		return graph.GetLinkAttributes(info);
	}

	@Override
	public void Delete()
	{
		graph.DeleteLink(info);
	}

	public GraphObject Target()
	{
		return new GraphObject(graph, graph.GetLinkTarget(info));
	}

	public GraphObject Source()
	{
		return new GraphObject(graph, graph.GetLinkSource(info));
	}

	public void DeleteAttribute(String name)
	{
		graph.DeleteLinkAttribute(info, name);
	}
}
