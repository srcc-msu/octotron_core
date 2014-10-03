package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.primitive.EEntityType;

public class ModelLink extends ModelEntity
{
	private ModelObject target;
	private ModelObject source;

	public ModelLink()
	{
		super(EEntityType.LINK);
	}

	public ModelObject Target()
	{
		return target;
	}

	public ModelObject Source()
	{
		return source;
	}
}
