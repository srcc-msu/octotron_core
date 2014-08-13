package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.primitive.Uid;

public abstract class GraphBased //implements IEntity
{
	private final GraphEntity base;

	public GraphBased(GraphEntity base)
	{
		this.base = base;
	}

//	@Override
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

	/*@Override
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
	public AttributeList<GraphAttribute> GetAttributes()
	{
		return base.GetAttributes();
	}
*/
//	@Override
	public Uid GetUID()
	{
		return base.GetUID();
	}
}
