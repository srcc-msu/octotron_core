package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.rule.OctoRule;

public class DerivedObjectFactory extends MetaObjectFactory<DerivedObject, OctoRule>
{
	@Override
	protected DerivedObject CreateInstance(GraphService graph_service, GraphObject meta_object)
	{
		return new DerivedObject(graph_service, meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.DERIVED.toString();
	}
}
