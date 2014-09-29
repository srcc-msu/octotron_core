package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.storage.PersistentStorage;
import ru.parallel.utils.JavaUtils;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class ReactionObject extends MetaObject
{
	private static final String reaction_id_const = "_id";
	private static final String reaction_state_const = "_status";
	private static final String reaction_start_time_const = "_started";
	private static final String reaction_repeat_const = "_repeat";
	private static final String reaction_stat_const = "_stat";

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
		GetBaseEntity().DeclareAttribute(reaction_stat_const, 0);
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

	private void SetState(long res)
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

	private void StartDelay()
	{
		GetBaseEntity().UpdateAttribute(reaction_start_time_const, JavaUtils.GetTimestamp());
	}

	private long GetDelay()
	{
		long value = GetAttribute(reaction_start_time_const).GetLong();

		if(value == 0) // timer not started
			return 0;

		return JavaUtils.GetTimestamp() - value;
	}

	private void DropDelay()
	{
		GetBaseEntity().UpdateAttribute(reaction_start_time_const, 0);
	}

// -------------

	public void Repeat(Object new_value)
	{
		if(new_value.equals(GetReaction().GetCheckValue()))
			GetBaseEntity().UpdateAttribute(reaction_repeat_const, GetRepeat() + 1);
		else
			DropRepeat();
	}

	private long GetRepeat()
	{
		return GetAttribute(reaction_repeat_const).GetLong();
	}

	private void DropRepeat()
	{
		GetBaseEntity().UpdateAttribute(reaction_repeat_const, 0);
	}

// -------------

	private void IncStat()
	{
		GetBaseEntity().UpdateAttribute(reaction_stat_const, GetStat() + 1);
	}

	private long GetStat()
	{
		return GetAttribute(reaction_stat_const).GetLong();
	}

	public void ResetStat()
	{
		GetBaseEntity().UpdateAttribute(reaction_stat_const, 0);
	}

// -------------

	@Nullable
	public Response Process(ModelEntity parent)
	{
		Reaction reaction = GetReaction();
		boolean needed = reaction.ReactionNeeded(parent);

		long state = GetState();

		if(needed)
		{
			long delay = reaction.GetDelay();
			long repeat = reaction.GetRepeat();

			long current_delay = GetDelay();
			long current_repeat = GetRepeat();

			boolean ready = (current_delay >= delay)
				&& (current_repeat >= repeat);

			if(state == Reaction.STATE_NONE)
			{
				if(ready)
				{
					IncStat();
					SetState(Reaction.STATE_EXECUTED);

					return reaction.GetResponse();
				}
				else
				{
					SetState(Reaction.STATE_STARTED);

					StartDelay();
				}
			}
			else if(state == Reaction.STATE_STARTED)
			{
				if(ready)
				{
					IncStat();
					SetState(Reaction.STATE_EXECUTED);

					return reaction.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
			else if(state == Reaction.STATE_EXECUTED)
			{
				if(reaction.IsRepeatable())
				{
					IncStat();
					return reaction.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
		}
		else
		{
			if(state == Reaction.STATE_NONE)
			{
				// nothing to see here
			}
			else if(state == Reaction.STATE_STARTED)
			{
				SetState(Reaction.STATE_NONE);
				DropDelay();
				DropRepeat();
			}
			else if(state == Reaction.STATE_EXECUTED)
			{
				SetState(Reaction.STATE_NONE);
				DropDelay();
				DropRepeat();

				if(reaction.GetRecoverResponse() != null)
					return reaction.GetRecoverResponse();
			}
		}

		return null;
	}
}
