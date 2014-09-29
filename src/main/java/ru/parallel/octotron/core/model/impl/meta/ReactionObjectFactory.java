package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.EObjectLabels;

public class ReactionObjectFactory extends MetaObjectFactory<ReactionObject, Reaction>
{
	private ReactionObjectFactory() { super(); }

	public static final ReactionObjectFactory INSTANCE = new ReactionObjectFactory();

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