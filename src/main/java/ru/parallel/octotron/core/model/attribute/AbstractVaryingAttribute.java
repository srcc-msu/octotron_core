/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.meta.AttributeObject;
import ru.parallel.octotron.core.model.meta.DerivedObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractVaryingAttribute<T extends AttributeObject> extends ModelAttribute
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	protected T meta;

	public AbstractVaryingAttribute(ModelEntity parent, T meta, String name)
	{
		super(parent, name);
		this.meta = meta;
	}

	@Override
	public ModelEntity GetParent()
	{
		return parent;
	}

	@Override
	public Object GetLastValue()
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
		long cur_ctime = GetCTime();
		long last_ctime = meta.GetLast().GetCTime();

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


	public void Notify(String id, long seconds)
	{

	}

	public void StopNotify(String id)
	{

	}

	public boolean ShouldNotify(String id, long current_time)
	{
		return false;
	}

	@Override
	public List<OctoResponse> PreparePendingReactions()
	{
		List<Marker> markers = meta.GetMarkers();

		List<OctoResponse> result = new LinkedList<>();

		long current_time = JavaUtils.GetTimestamp();

		for(OctoReaction reaction : meta.GetReactions())
		{
			Marker skip_marker = null;
			for(Marker marker : markers)
			{
				if(marker.GetTarget() == reaction.GetID() && marker.IsSuppress())
				{
					skip_marker = marker;
					break;
				}
			}

			boolean needed = reaction.ReactionNeeded(parent);
			long state = meta.GetReactionState(reaction);

			if(needed && skip_marker != null)
			{
				LOGGER.log(Level.FINE, "reaction suppressed: " + skip_marker.GetDescription()
					+ " reaction id:" + skip_marker.GetTarget()
					+ " time: " + current_time);
				continue;
			}

			if(needed)
			{
				if(state == OctoReaction.STATE_NONE)
				{
					long delay = reaction.GetDelay();

					if(delay == 0)
					{
						result.add(reaction.GetResponse());

						meta.SetReactionState(reaction, OctoReaction.STATE_EXECUTED);
					}
					else
					{
						meta.SetReactionState(reaction, OctoReaction.STATE_STARTED);

						Notify(OctoReaction.DELAY_PREFIX
							+ reaction.GetID(), delay + 1);
					}
				}
				else if(state == OctoReaction.STATE_STARTED)
				{
					if(ShouldNotify(OctoReaction.DELAY_PREFIX
						+ reaction.GetID(), current_time))
					{
						result.add(reaction.GetResponse());

						meta.SetReactionState(reaction, OctoReaction.STATE_EXECUTED);

						StopNotify(OctoReaction.DELAY_PREFIX
							+ reaction.GetID());
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
					meta.SetReactionState(reaction, OctoReaction.STATE_NONE);

					StopNotify(OctoReaction.DELAY_PREFIX
						+ reaction.GetID());
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					meta.SetReactionState(reaction, OctoReaction.STATE_NONE);
				}
			}
		}

		return result;
	}

	@Override
	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> fails = new LinkedList<>();

		for(OctoReaction reaction : meta.GetReactions())
		{
			boolean needed = reaction.ReactionNeeded(parent);

			if(needed)
				fails.add(reaction.GetResponse());
		}

		return fails;
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
	public AttributeList<DerivedAttribute> GetDependant()
	{
		AttributeList<DerivedAttribute> result = new AttributeList<>();

		for(GraphObject object : meta.GetBaseObject().GetInNeighbors(dependence_link))
		{
			AttributeObject attribute_object = new DerivedObject(meta.GetGraphService(), object);

			result.add((DerivedAttribute)attribute_object.GetAttribute());
		}

		return result;
	}
}
