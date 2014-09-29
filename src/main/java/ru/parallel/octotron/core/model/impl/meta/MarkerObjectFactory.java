package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class MarkerObjectFactory extends MetaObjectFactory<MarkerObject, Marker>
{
	private MarkerObjectFactory() { super(); }

	public static final MarkerObjectFactory INSTANCE = new MarkerObjectFactory();

	@Override
	protected MarkerObject CreateInstance(GraphObject meta_object)
	{
		return new MarkerObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.MARK.toString();
	}
}