package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

public class GraphBased implements IEntity
{
	protected final GraphEntity base;

	public GraphBased(GraphEntity base)
	{
		this.base = base;
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
	public IAttribute GetAttribute(String name)
	{
		return base.GetAttribute(name);
	}

	@Override
	public AttributeList<AbstractVaryingAttribute> GetAttributes()
	{
		return base.GetAttributes();
	}

	@Override
	public GraphAttribute SetAttribute(String name, Object value)
	{
		return base.SetAttribute(name, value);
	}

	@Override
	public GraphAttribute SetAttribute(SimpleAttribute att)
	{
		return base.SetAttribute(att);
	}

	@Override
	public void RemoveAttribute(String name)
	{
		base.RemoveAttribute(name);
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
	public boolean TestAttribute(SimpleAttribute test)
	{
		return base.TestAttribute(test);
	}

	@Override
	public GraphAttribute DeclareAttribute(String name, Object value)
	{
		return base.DeclareAttribute(name, value);
	}

	@Override
	public GraphAttribute DeclareAttribute(SimpleAttribute att)
	{
		return base.DeclareAttribute(att);
	}

	@Override
	public Uid GetUID()
	{
		return base.GetUID();
	}

	@Override
	public void Delete()
	{
		base.Delete();
	}
}
