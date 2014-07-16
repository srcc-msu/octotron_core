package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphService;

public class ModelLink extends ModelEntity implements ILink
{
	public ModelLink(GraphService graph_service, GraphLink link)
	{
		super(graph_service, link);
	}

	@Override
	public ModelObject Target()
	{
		return new ModelObject(GetGraphService(), GetBaseLink().Target());
	}

	@Override
	public ModelObject Source()
	{
		return new ModelObject(GetGraphService(), GetBaseLink().Source());
	}
}
