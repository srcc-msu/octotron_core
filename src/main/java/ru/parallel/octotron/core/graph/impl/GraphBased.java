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

	public final GraphEntity GetBaseEntity()
	{
		return base;
	}

	public final GraphLink GetBaseLink()
	{
		return (GraphLink)base;
	}

	public final GraphObject GetBaseObject()
	{
		return (GraphObject)base;
	}

	public final Uid GetUID()
	{
		return base.GetUID();
	}
}
