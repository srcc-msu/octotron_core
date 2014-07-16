package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;

public abstract class MetaObject extends GraphBased
{
	public MetaObject(GraphService graph_service, GraphEntity base)
	{
		super(graph_service, base);
	}

	public abstract void Init(Object object);
}
