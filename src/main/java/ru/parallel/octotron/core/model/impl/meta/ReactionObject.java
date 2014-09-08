package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.storage.PersistentStorage;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;

public class ReactionObject extends MetaObject
{
	private static final String reaction_id_const = "_id";
	private static final String reaction_state_const = "_status";
	private static final String reaction_start_time_const = "_started";
	private static final String reaction_repeat_const = "_repeat";

	public ReactionObject(GraphEntity base)
	{
		super(base);
	}

	@Override
	public void Init(Object object)
	{
		Reaction reaction = (Reaction) object;

		GetBaseObject().DeclareAttribute(reaction_id_const, reaction.GetID());
		GetBaseObject().DeclareAttribute(reaction_state_const, Reaction.STATE_NONE);
		GetBaseEntity().DeclareAttribute(reaction_start_time_const, 0);
		GetBaseEntity().DeclareAttribute(reaction_repeat_const, 0);
	}

	public long GetID()
	{
		return GetAttribute(reaction_id_const).GetLong();
	}

	public long GetState()
	{
		return GetAttribute(reaction_state_const).GetLong();
	}

	public Reaction GetReaction()
	{
		return PersistentStorage.INSTANCE.GetReactions()
			.Get(GetAttribute(reaction_id_const).GetLong());
	}

	public void SetState(long res)
	{
		GetBaseObject().UpdateAttribute(reaction_state_const, res);
	}

	public long AddMarker(String description, boolean suppress)
	{
		MarkerObject marker_object = MarkerObjectFactory.INSTANCE
			.Create(GetBaseEntity(), new Marker(GetReaction().GetID(), description, suppress));

		return marker_object.GetAttribute("AID").GetLong();
	}

	public List<Marker> GetMarkers()
	{
		List<Marker> result = new LinkedList<>();

		for(MarkerObject marker_object : MarkerObjectFactory.INSTANCE
			.ObtainAll(GetBaseEntity()))
			result.add(marker_object.GetMarker());

		return result;
	}

	public void TryDeleteMarker(long id)
	{
		for(MarkerObject marker_object : MarkerObjectFactory.INSTANCE
			.ObtainAll(GetBaseEntity()))
			if(marker_object.GetAttribute("AID").eq(id))
				marker_object.GetBaseEntity().Delete();
	}

	public void StartDelay()
	{
		GetBaseEntity().UpdateAttribute(reaction_start_time_const, JavaUtils.GetTimestamp());
	}

	public long GetDelay()
	{
		long value = GetAttribute(reaction_start_time_const).GetLong();

		if(value == 0) // timer not started
			return 0;

		return JavaUtils.GetTimestamp() - value;
	}

	public void DropDelay()
	{
		GetBaseEntity().UpdateAttribute(reaction_start_time_const, 0);
	}

// -------------

	public void Repeat(Object new_value)
	{
		if(new_value == GetReaction().GetCheckValue())
			GetBaseEntity().UpdateAttribute(reaction_repeat_const, GetRepeat() + 1);
		else
			DropRepeat();
	}

	public long GetRepeat()
	{
		return GetAttribute(reaction_repeat_const).GetLong();
	}

	public void DropRepeat()
	{
		GetBaseEntity().UpdateAttribute(reaction_repeat_const, 0);
	}

}
