/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

/**
 * implementation of link, that resides in \graph<br>
 * */
public final class GraphLink extends GraphEntity implements ILink, IEntity
{
	public GraphLink(IGraph graph, Uid uid)
	{
		super(graph, uid);
	}

	@Override
	public GraphAttribute GetAttribute(String name)
	{
		return new GraphAttribute(this, name);
	}

	@Override
	public Object GetRawAttribute(String name)
	{
		return graph.GetLinkAttribute(uid, name);
	}

	@Override
	public AttributeList GetAttributes()
	{
		return GraphService.AttributesFromPair(this, graph.GetLinkAttributes(uid));
	}

	@Override
	public GraphAttribute SetAttribute(String name, Object value)
	{
		graph.SetLinkAttribute(uid, name, value);
		return new GraphAttribute(this, name);
	}

	@Override
	public void RemoveAttribute(String name)
	{
		graph.DeleteLinkAttribute(uid, name);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestLinkAttribute(uid, name);
	}

	@Override
	public void Delete()
	{
		graph.DeleteLink(uid);
	}

	@Override
	public GraphObject Target()
	{
		return new GraphObject(graph, graph.GetLinkTarget(uid));
	}

	@Override
	public GraphObject Source()
	{
		return new GraphObject(graph, graph.GetLinkSource(uid));
	}
}
