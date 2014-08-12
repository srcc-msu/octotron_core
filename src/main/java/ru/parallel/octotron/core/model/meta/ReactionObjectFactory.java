package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class ReactionObjectFactory extends MetaObjectFactory<ReactionObject, OctoReaction>
{
	@Override
	protected ReactionObject CreateInstance(GraphObject meta_object)
	{
		return new ReactionObject(meta_object);
	}

	@Override
	protected String GetLabel()
	{
		return EObjectLabels.REACTION.toString();
	}
}