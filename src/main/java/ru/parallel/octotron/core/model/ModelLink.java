package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphService;

public class ModelLink extends ModelEntity implements ILink
{
	public ModelLink(GraphLink link)
	{
		super(link);
	}

	@Override
	public ModelObject Target()
	{
		return new ModelObject(GetBaseLink().Target());
	}

	@Override
	public ModelObject Source()
	{
		return new ModelObject(GetBaseLink().Source());
	}
}
