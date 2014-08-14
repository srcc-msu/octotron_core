package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.OctoRule;

public class VariableObjectFactory extends MetaObjectFactory<VariableObject, OctoRule>
{
	@Override
	protected VariableObject CreateInstance(GraphObject meta_object)
	{
		return new VariableObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.VARIABLE.toString();
	}
}
