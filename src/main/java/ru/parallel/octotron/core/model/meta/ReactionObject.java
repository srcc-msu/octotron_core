package ru.parallel.octotron.core.model.meta;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.impl.PersistentStorage;

public class ReactionObject extends MetaObject
{
	private static final String reaction_id_const = "_id";
	private static final String reaction_state_const = "_status";

	public ReactionObject(GraphEntity base)
	{
		super(base);
	}

	@Override
	public void Init(Object object)
	{
		OctoReaction reaction = (OctoReaction) object;

		GetBaseObject().DeclareAttribute(reaction_id_const, reaction.GetID());
		GetBaseObject().DeclareAttribute(reaction_state_const, OctoReaction.STATE_NONE);
	}

	public long GetID()
	{
		return GetBaseObject().GetAttribute(reaction_id_const).GetLong();
	}

	public long GetState()
	{
		return GetBaseObject().GetAttribute(reaction_state_const).GetLong();
	}

	public OctoReaction GetReaction()
	{
		return PersistentStorage.INSTANCE.GetReactions()
			.Get(GetBaseObject().GetAttribute(reaction_id_const).GetLong());
	}

	public void SetState(long res)
	{
		GetBaseObject().UpdateAttribute(reaction_state_const, res);
	}
}
