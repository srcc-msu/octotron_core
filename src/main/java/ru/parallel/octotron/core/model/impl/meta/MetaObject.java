package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public abstract class MetaObject extends GraphBased
{
	public MetaObject(GraphEntity base)
	{
		super(base);
	}

	public abstract void Init(Object object);

	public boolean TestAttribute(String name)
	{
		return GetBaseEntity().TestAttribute(name);
	}

	public boolean TestAttribute(String name, Object value)
	{
		return GetBaseEntity().TestAttribute(name, value);
	}

	public boolean TestAttribute(SimpleAttribute attribute)
	{
		return GetBaseEntity().TestAttribute(attribute);
	}

	public GraphAttribute GetAttribute(String name)
	{
		return GetBaseEntity().GetAttribute(name);
	}

	public AttributeList<GraphAttribute> GetAttributes()
	{
		return GetBaseEntity().GetAttributes();
	}
}
