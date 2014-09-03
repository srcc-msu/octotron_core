package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.primitive.Uid;

public abstract class GraphBased //implements IEntity
{
	private final GraphEntity base;

	public GraphBased(GraphEntity base)
	{
		this.base = base;
	}

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

	public Uid GetUID()
	{
		return base.GetUID();
	}
}
