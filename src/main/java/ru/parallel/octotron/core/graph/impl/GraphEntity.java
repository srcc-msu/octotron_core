/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.EGraphType;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.UniqueID;
/**
 * some entity, that resides in model_old<br>
 * all operations with it go through the \graph interface, no caching<br>
 * */
abstract class GraphEntity
{
	protected final IGraph graph;
	protected final UniqueID<EGraphType> id;

	public GraphEntity(IGraph graph, long id, EGraphType type)
	{
		this.graph = graph;
		this.id = new UniqueID<>(id, type);
	}

	public GraphEntity(IGraph graph, UniqueID<EGraphType> id)
	{
		this.graph = graph;
		this.id = id;
	}

	public abstract void Delete();

// --------------------------------
//			ATTRIBUTES
//---------------------------------

	public abstract Object GetAttribute(String name);
	public abstract boolean TestAttribute(String name);

	public boolean TestAttribute(String name, Object value)
	{
		return TestAttribute(name) && GetAttribute(name).equals(value);
	}
	public boolean TestAttribute(SimpleAttribute test)
	{
		return TestAttribute(test.GetName(), test.GetValue());
	}

// ---

	public abstract void UpdateAttribute(String name, Object value);
	public void UpdateAttribute(SimpleAttribute att)
	{
		UpdateAttribute(att.GetName(), att.GetValue());
	}

	public UniqueID<EGraphType> GetID()
	{
		return id;
	}
}