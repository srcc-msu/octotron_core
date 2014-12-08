/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.EGraphType;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.primitive.ID;

import java.util.List;

/**
 * some entity, that resides in model_old<br>
 * all operations with it go through the \graph interface, no caching<br>
 * */
public abstract class GraphEntity
{
	protected final IGraph graph;
	protected final ID<EGraphType> id;

	public GraphEntity(IGraph graph, long id, EGraphType type)
	{
		this.graph = graph;
		this.id = new ID<>(id, type);
	}

	public GraphEntity(IGraph graph, ID<EGraphType> id)
	{
		this.graph = graph;
		this.id = id;
	}

	public abstract void Delete();

// --------------------------------
//			ATTRIBUTES
//---------------------------------

	public abstract List<String> GetAttributes();
	public abstract Object GetAttribute(String name);
	public abstract boolean TestAttribute(String name);

	public boolean TestAttribute(String name, Object value)
	{
		return TestAttribute(name) && GetAttribute(name).equals(value);
	}

// ---

	public abstract void UpdateAttribute(String name, Object value);

	public ID<EGraphType> GetID()
	{
		return id;
	}
}