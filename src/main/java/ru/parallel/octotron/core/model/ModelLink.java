package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.impl.GraphLink;

public class ModelLink extends ModelEntity
{
	public ModelLink(GraphLink link)
	{
		super(link);
	}

	public ModelObject Target()
	{
		return new ModelObject(GetBaseLink().Target());
	}

	public ModelObject Source()
	{
		return new ModelObject(GetBaseLink().Source());
	}
}
