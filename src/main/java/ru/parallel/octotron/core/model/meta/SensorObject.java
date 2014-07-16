package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class SensorObject extends AttributeObject
{
	public SensorObject(GraphService graph_service, GraphObject object)
	{
		super(graph_service, object);
	}

	@Override
	public void Init(Object object)
	{
		SimpleAttribute attribute = (SimpleAttribute)object;
		super.Init(GetBaseObject(), attribute.GetName(), attribute.GetValue());
	}
}
