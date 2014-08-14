package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.impl.GraphLink;

public class ModelLink extends ModelEntity implements ILink<ModelAttribute>
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
