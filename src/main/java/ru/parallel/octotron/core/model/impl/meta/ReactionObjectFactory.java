package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.rule.OctoReaction;

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