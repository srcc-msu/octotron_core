package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.neo4j.impl.Marker;

public class MarkerObjectFactory extends MetaObjectFactory<MarkerObject, Marker>
{
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