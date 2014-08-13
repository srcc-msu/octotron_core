/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.meta.*;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractVaryingAttribute<T extends AttributeObject> extends ModelAttribute
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	protected final T meta;

	public AbstractVaryingAttribute(ModelEntity parent, T meta, String name)
	{
		super(parent, name);
		this.meta = meta;
	}

	@Override
	public HistoryObject GetLastValue()
	{
		return meta.GetLast();
	}

	@Override
	public long GetCTime()
	{
		return meta.GetCTime();
	}

	@Override
	public long GetATime()
	{
		return meta.GetATime();
	}

	@Override
	public double GetSpeed()
	{
// check time
		HistoryObject last = meta.GetLast();
		if(last == null)
			return 0.0;

		long cur_ctime = GetCTime();
		long last_ctime = last.GetCTime();

		if(cur_ctime - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - meta.GetLast().GetValue().ToDouble();

		return diff / (cur_ctime - last_ctime);
	}

	protected void RotateValue(Object new_value, long cur_time)
	{
		meta.SetLast(GetValue(), GetCTime());

		SetValue(new_value);
		meta.SetCurrent(new_value, cur_time);
	}

	protected void Touch(long cur_time)
	{
		meta.Touch(cur_time);
	}

	@Override
	protected boolean Update(Object new_value, boolean allow_overwrite)
	{
		long cur_time = JavaUtils.GetTimestamp();
		Touch(cur_time);

// if got a new value, or was not initialized or allow_overwrite is on
		if(ne(new_value) || GetCTime() == 0 || allow_overwrite)
		{
			RotateValue(new_value, cur_time);
			return true;
		}

		return false;
	}

	private List<ReactionObject> FilterSuppressed(List<ReactionObject> reaction_objects)
	{
		List<ReactionObject> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
		{
			boolean suppress = false;
			for(Marker marker : reaction_object.GetMarkers())
			{
				if(marker.IsSuppress())
				{
					suppress = true;
					break;
				}
			}

			if(!suppress)
				result.add(reaction_object);
		}

		return result;
	}


	@Override
	public List<OctoResponse> GetExecutedReactions()
	{
		List<OctoResponse> result = new LinkedList<>();

		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		reaction_objects = FilterSuppressed(reaction_objects);

		for(ReactionObject reaction_object : reaction_objects)
		{
			if(reaction_object.GetState() == OctoReaction.STATE_EXECUTED)
				result.add(reaction_object.GetReaction().GetResponse());
		}

		return result;
	}

	public List<OctoResponse> GetReadyReactions()
	{
		List<OctoResponse> result = new LinkedList<>();

		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		reaction_objects = FilterSuppressed(reaction_objects);

		for(ReactionObject reaction_object : reaction_objects)
		{
			OctoReaction reaction = reaction_object.GetReaction();

			boolean needed = reaction.ReactionNeeded(parent);
			long state = reaction_object.GetState();

			if(needed)
			{
				long delay = reaction.GetDelay();
				long repeat = reaction.GetRepeat();

				boolean ready = (reaction_object.GetDelay() > delay)
					&& (reaction_object.GetRepeat() > repeat);

				if(state == OctoReaction.STATE_NONE)
				{
					if(ready)
					{
						result.add(reaction.GetResponse());

						reaction_object.SetState(OctoReaction.STATE_EXECUTED);
					}
					else
					{
						reaction_object.SetState(OctoReaction.STATE_STARTED);

						reaction_object.StartDelay();
						reaction_object.StartRepeat();
					}
				}
				else if(state == OctoReaction.STATE_STARTED)
				{
					if(ready)
					{
						result.add(reaction.GetResponse());

						reaction_object.SetState(OctoReaction.STATE_EXECUTED);
					}
					else
					{
						reaction_object.Repeat();
					}
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					// nothing to see here
				}
			}
			else
			{
				if(state == OctoReaction.STATE_NONE)
				{
					// nothing to see here
				}
				else if(state == OctoReaction.STATE_STARTED)
				{
					reaction_object.SetState(OctoReaction.STATE_NONE);
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					reaction_object.SetState(OctoReaction.STATE_NONE);
				}
			}
		}

		return result;
	}

//-------------------
//      INVALID
//-------------------

	@Override
	public boolean IsValid()
	{
		return meta.GetValid() && GetCTime() != 0;
	}

	@Override
	public void SetValid()
	{
		meta.SetValid(true);
	}

	@Override
	public void SetInvalid()
	{
		meta.SetValid(false);
	}

	private static final SimpleAttribute dependence_link
		= new SimpleAttribute("type", "_depends");

	@Override
	public AttributeList<VariableAttribute> GetDependant()
	{
		AttributeList<VariableAttribute> result = new AttributeList<>();

		for(GraphObject object : meta.GetBaseObject().GetInNeighbors(dependence_link))
		{
			VariableObject attribute_object = new VariableObject(object); // only rules will be here

			result.add((VariableAttribute)attribute_object.GetParentAttribute());
		}

		return result;
	}

	@Override
	public void AddReaction(OctoReaction reaction)
	{
		new ReactionObjectFactory().Create(meta.GetBaseEntity(), reaction);
	}

	@Override
	public List<OctoReaction> GetReactions()
	{
		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		List<OctoReaction> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
			result.add(reaction_object.GetReaction());

		return result;
	}

	@Override
	public long AddMarker(OctoReaction reaction, String description, boolean suppress)
	{
		ReactionObject reaction_object = new ReactionObjectFactory()
			.Create(meta.GetBaseEntity(), reaction);

		return reaction_object.AddMarker(description, suppress);
	}

	@Override
	public List<Marker> GetMarkers()
	{
		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		List<Marker> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
			result.addAll(reaction_object.GetMarkers());

		return result;
	}

	@Override
	public void DeleteMarker(long id)
	{
		List<ReactionObject> reaction_objects = new ReactionObjectFactory()
			.ObtainAll(meta.GetBaseEntity());

		for(ReactionObject reaction_object : reaction_objects)
			reaction_object.TryDeleteMarker(id);
	}
}
