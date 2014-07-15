package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public abstract class GraphBased implements IEntity
{
	private final GraphService graph_service;
	private final GraphEntity base;

	public GraphService GetGraphService()
	{
		return graph_service;
	}

	public GraphBased(GraphService graph_service, GraphEntity base)
	{
		this.graph_service = graph_service;
		this.base = base;
	}

	@Override
	public final boolean equals(Object object)
	{
		if (!(object instanceof GraphBased))
			return false;

		return GetUID().equals(((GraphBased) object).GetUID());
	}

	public GraphEntity GetBaseEntity()
	{
		return base;
	}

	public GraphLink GetBaseLink()
	{
		return (GraphLink)base;
	}

	public GraphObject GetBaseObject()
	{
		return (GraphObject)base;
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return base.TestAttribute(name);
	}

	@Override
	public boolean TestAttribute(String name, Object value)
	{
		return base.TestAttribute(name, value);
	}

	@Override
	public boolean TestAttribute(SimpleAttribute attribute)
	{
		return base.TestAttribute(attribute);
	}

	@Override
	public IAttribute GetAttribute(String name)
	{
		return base.GetAttribute(name);
	}

	@Override
	public AttributeList GetAttributes()
	{
		return base.GetAttributes();
	}

	@Override
	public Uid GetUID()
	{
		return base.GetUID();
	}
}
