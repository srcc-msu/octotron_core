/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

/**
 * implementation of object according to real \graph<br>
 * */
public final class GraphObject extends GraphEntity implements IObject<GraphAttribute>
{
	public GraphObject(IGraph graph, Uid uid)
	{
		super(graph, uid);
	}

	@Override
	public GraphAttribute GetAttribute(String name)
	{
		return new GraphAttribute(this, name);
	}

	@Override
	public AttributeList<GraphAttribute> GetAttributes()
	{
		return GraphService.AttributesFromPair(this, graph.GetObjectAttributes(uid));
	}

	@Override
	Object GetRawAttribute(String name)
	{
		return graph.GetObjectAttribute(uid, name);
	}

	public void DeleteAttribute(String name)
	{
		graph.DeleteObjectAttribute(uid, name);
	}

	@Override
	public GraphAttribute UpdateAttribute(String name, Object value)
	{
		value = SimpleAttribute.ConformType(value);

		if(!TestAttribute(name))
			throw new ExceptionModelFail("attribute not found: " + name);

		GetAttribute(name).CheckType(value);
		graph.SetObjectAttribute(uid, name, value);
		return new GraphAttribute(this, name);
	}

	@Override
	public GraphAttribute DeclareAttribute(String name, Object value)
	{
		value = SimpleAttribute.ConformType(value);

		if(TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		graph.SetObjectAttribute(uid, name, value);
		return new GraphAttribute(this, name);
	}

	@Override
	public void AddLabel(String label)
	{
		graph.AddNodeLabel(uid, label);
	}

	@Override
	public boolean TestLabel(String label)
	{
		return graph.TestNodeLabel(uid, label);
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return graph.TestObjectAttribute(uid, name);
	}

	@Override
	public void Delete()
	{
		graph.DeleteObject(uid);
	}

// ----
//
// ----

	@Override
	public GraphLinkList GetInLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetInLinks(uid));
	}

	@Override
	public GraphLinkList GetOutLinks()
	{
		return GraphService.LinksFromUid(graph, graph.GetOutLinks(uid));
	}

	@Override
	public GraphObjectList GetInNeighbors()
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetInLinks())
			objects.add(link.Source());

		return objects;
	}

	@Override
	public GraphObjectList GetOutNeighbors()
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetOutLinks())
			objects.add(link.Target());

		return objects;
	}

	@Override
	public GraphObjectList GetInNeighbors(String link_name
		, Object link_value)
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetInLinks().Filter(link_name, link_value))
			objects.add(link.Source());

		return objects;
	}

	@Override
	public GraphObjectList GetOutNeighbors(String link_name
		, Object link_value)
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetOutLinks().Filter(link_name, link_value))
			objects.add(link.Target());

		return objects;
	}

	@Override
	public GraphObjectList GetInNeighbors(String link_name)
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetInLinks().Filter(link_name))
			objects.add(link.Source());

		return objects;
	}

	@Override
	public GraphObjectList GetOutNeighbors(String link_name)
	{
		GraphObjectList objects = new GraphObjectList();

		for(GraphLink link : GetOutLinks().Filter(link_name))
			objects.add(link.Target());

		return objects;
	}

	@Override
	public GraphObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	@Override
	public GraphObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}
}
