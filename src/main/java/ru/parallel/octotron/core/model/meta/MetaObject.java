package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;

public abstract class MetaObject extends GraphBased
{
	public MetaObject(GraphEntity base)
	{
		super(base);
	}

	public abstract void Init(Object object);
}
