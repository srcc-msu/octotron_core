/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;

/**
 * some entity, that resides in model<br>
 * all operations with it go through the \graph interface, no caching<br>
 * */
public abstract class GraphEntity implements IEntity
{
	protected final IGraph graph;
	protected final Uid uid;

	public GraphEntity(IGraph graph, Uid uid)
	{
		this.graph = graph;
		this.uid = uid;
	}

	public final Uid GetUID()
	{
		return uid;
	}

	@Override
	public final boolean equals(Object object)
	{
		if (!(object instanceof GraphEntity))
			return false;

		return uid.equals(((GraphEntity) object).GetUID());
	}

	public abstract void Delete();

// --------------------------------
//			ATTRIBUTES
//---------------------------------

	@Override
	public abstract GraphAttribute GetAttribute(String name);
	@Override
	public abstract AttributeList<GraphAttribute> GetAttributes();

	@Override
	public boolean TestAttribute(String name, Object value)
	{
		return TestAttribute(name) && GetAttribute(name).eq(value);
	}
	@Override
	public boolean TestAttribute(SimpleAttribute test)
	{
		return TestAttribute(test.GetName(), test.GetValue());
	}

// ---

	abstract Object GetRawAttribute(String name);

	public abstract GraphAttribute UpdateAttribute(String name, Object value);
	public GraphAttribute UpdateAttribute(SimpleAttribute att)
	{
		return UpdateAttribute(att.GetName(), att.GetValue());
	}

	public abstract GraphAttribute DeclareAttribute(String name, Object value);
	public GraphAttribute DeclareAttribute(SimpleAttribute att)
	{
		return DeclareAttribute(att.GetName(), att.GetValue());
	}

// --------------------------------
//			ATTRIBUTES
//---------------------------------

	public abstract void AddLabel(String label);
	public abstract boolean TestLabel(String label);
}