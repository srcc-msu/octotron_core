package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.Persistent;

public class ModelLink extends ModelEntity
{
	public static class ModelLinkBuilder extends ModelEntityBuilder<ModelLink>
	{
		ModelLinkBuilder(ModelLink entity)
		{
			super(entity);
		}
	}

	@Override
	public void InitPersistent()
	{
		persistent = ModelService.Get()
			.GetPersistentLink(this
				, (GraphObject)Source().GetPersistent()
				, (GraphObject)Target().GetPersistent()
				, "link");
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
