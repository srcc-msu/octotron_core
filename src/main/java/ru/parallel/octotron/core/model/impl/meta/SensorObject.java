package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class SensorObject extends AttributeObject
{
	public SensorObject(GraphObject object)
	{
		super(object);
	}

	@Override
	public void Init(Object object)
	{
		SimpleAttribute attribute = (SimpleAttribute)object;
		Init(GetBaseObject());
	}
}
