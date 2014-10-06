package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.primitive.EEntityType;

public class ModelLink extends ModelEntity
{
	public static class ModelLinkBuilder extends ModelEntityBuilder<ModelLink>
	{
		ModelLinkBuilder(ModelLink entity)
		{
			super(entity);
		}
	}

	private ModelObject source;
	private ModelObject target;

	public ModelLink(ModelObject source, ModelObject target)
	{
		super(EEntityType.LINK);
		this.target = target;
		this.source = source;
	}

	public ModelObject Target()
	{
		return target;
	}

	public ModelObject Source()
	{
		return source;
	}

	@Override
	public ModelLinkBuilder GetBuilder()
	{
		return new ModelLinkBuilder(this);
	}
}
