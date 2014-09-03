/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.*;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractVaryingAttribute<T extends AttributeObject> extends ModelAttribute implements IMetaAttribute
{
	protected final T meta;

	public AbstractVaryingAttribute(ModelEntity parent, GraphAttribute attribute, T meta)
	{
		super(parent, attribute);
		this.meta = meta;
	}

	public T GetMeta()
	{
		return meta;
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

		double diff = ToDouble() - last.GetValue().ToDouble();

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

		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

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

		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

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

				long current_delay = reaction_object.GetDelay();
				long current_repeat = reaction_object.GetRepeat();

				boolean ready = (current_delay >= delay)
					&& (current_repeat >= repeat);

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
						// nothing to see here
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
					reaction_object.DropDelay();
					reaction_object.DropRepeat();
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					reaction_object.SetState(OctoReaction.STATE_NONE);
					reaction_object.DropDelay();
					reaction_object.DropRepeat();
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
	public void AddDependant(VaryingAttribute attribute)
	{
		GraphLink link = GraphService.Get().AddLink(meta.GetBaseObject()
			, attribute.GetMeta().GetBaseObject()
			, dependence_link);

		link.DeclareAttribute(dependence_link);

	}

	@Override
	public AttributeList<VaryingAttribute> GetDependant()
	{
		AttributeList<VaryingAttribute> result = new AttributeList<>();

		for(GraphObject object : meta.GetBaseObject().GetOutNeighbors(dependence_link).Uniq())
		{
			VaryingObject attribute_object = new VaryingObject(object); // only rules will be here

			result.add((VaryingAttribute)attribute_object.GetParentAttribute());
		}

		return result;
	}

	@Override
	public void AddReaction(OctoReaction reaction)
	{
		ReactionObjectFactory.INSTANCE.Create(meta.GetBaseEntity(), reaction);
	}

	@Override
	public List<OctoReaction> GetReactions()
	{
		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		List<OctoReaction> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
			result.add(reaction_object.GetReaction());

		return result;
	}

	@Override
	public long AddMarker(OctoReaction reaction, String description, boolean suppress)
	{
		ReactionObject reaction_object = ReactionObjectFactory
			.INSTANCE.Create(meta.GetBaseEntity(), reaction);

		return reaction_object.AddMarker(description, suppress);
	}

	@Override
	public List<Marker> GetMarkers()
	{
		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		List<Marker> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
			result.addAll(reaction_object.GetMarkers());

		return result;
	}

	@Override
	public void DeleteMarker(long id)
	{
		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		for(ReactionObject reaction_object : reaction_objects)
			reaction_object.TryDeleteMarker(id);
	}
}