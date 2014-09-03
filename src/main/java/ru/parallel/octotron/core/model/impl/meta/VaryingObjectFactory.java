package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class VaryingObjectFactory extends MetaObjectFactory<VaryingObject, OctoRule>
{
	private VaryingObjectFactory() { super(); }

	public static final VaryingObjectFactory INSTANCE = new VaryingObjectFactory();

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
