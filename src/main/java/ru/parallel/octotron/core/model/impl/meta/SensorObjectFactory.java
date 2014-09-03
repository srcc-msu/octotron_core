package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class SensorObjectFactory extends MetaObjectFactory<SensorObject, SimpleAttribute>
{
	private SensorObjectFactory() { super(); }

	public static final SensorObjectFactory INSTANCE = new SensorObjectFactory();

	@Override
	protected SensorObject CreateInstance(GraphObject meta_object)
	{
		return new SensorObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.SENSOR.toString();
	}
}