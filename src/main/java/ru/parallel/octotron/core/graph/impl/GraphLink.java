/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

/**
 * implementation of link, that resides in \graph<br>
 * */
public final class GraphLink extends GraphEntity implements ILink<GraphAttribute>
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
	public AttributeList<GraphAttribute> GetAttributes()
	{
		return GraphService.AttributesFromPair(this, graph.GetLinkAttributes(uid));
	}

	@Override
	public GraphAttribute UpdateAttribute(String name, Object value)
	{
		value = SimpleAttribute.ConformType(value);

		if(!TestAttribute(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		GetAttribute(name).CheckType(value);
		graph.SetLinkAttribute(uid, name, value);
		return new GraphAttribute(this, name);
	}

	@Override
	public GraphAttribute DeclareAttribute(String name, Object value)
	{
		value = SimpleAttribute.ConformType(value);

		if(TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		graph.SetLinkAttribute(uid, name, value);
		return new GraphAttribute(this, name);
	}

	@Override
	public void AddLabel(String label)
	{
		throw new ExceptionModelFail("labels on links are not supported yet");
	}

	@Override
	public boolean TestLabel(String label)
	{
		throw new ExceptionModelFail("labels on links are not supported yet");
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

	public void DeleteAttribute(String name)
	{
		graph.DeleteLinkAttribute(uid, name);
	}
}
