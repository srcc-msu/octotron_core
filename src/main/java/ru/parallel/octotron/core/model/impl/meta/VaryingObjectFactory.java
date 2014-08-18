package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.OctoRule;

public class VaryingObjectFactory extends MetaObjectFactory<VaryingObject, OctoRule>
{
	@Override
	protected VaryingObject CreateInstance(GraphObject meta_object)
	{
		return new VaryingObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.VARYING.toString();
	}
}
