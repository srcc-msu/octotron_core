/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractVaryingAttribute extends ModelAttribute
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	protected final AttributeObject meta;

	static final String name_const = "_name";
	static final String value_const = "_value";
	static final String atime_const = "_atime";
	static final String ctime_const = "_ctime";
	static final String valid_const = "_valid";

	public AbstractVaryingAttribute(ModelEntity parent, String name)
	{
		super(parent, name);
		this.meta = GetMeta();
	}

	protected abstract AttributeObject GetMeta();

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
		return meta.GetAttribute(ctime_const).GetLong();
	}

	@Override
	public long GetATime()
	{
		return meta.GetAttribute(atime_const).GetLong();
	}

	@Override
	public double GetSpeed()
	{
		GraphObject last = meta.GetLast();

		if(last == null)
			return 0.0;

// check time
		long cur_ctime = GetCTime();
		long last_ctime = last.GetAttribute(ctime_const).GetLong();

		if(cur_ctime - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - last.GetAttribute(value_const).ToDouble();

		return diff / (cur_ctime - last_ctime);
	}

	private static final SimpleAttribute history_link
		= new SimpleAttribute("type", "_history");

	protected void RotateValue(Object new_value, long cur_time)
	{
		meta.SetLast(GetValue(), GetCTime());

		SetValue(new_value);
		meta.SetAttribute(ctime_const, cur_time);
	}

	protected void Touch(long cur_time)
	{
		meta.SetAttribute(atime_const, cur_time);
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
			long state = meta.GetReactionState(reaction.GetID());

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

						meta.SetReactionState(reaction.GetID(), OctoReaction.STATE_EXECUTED);
					}
					else
					{
						meta.SetReactionState(reaction.GetID(), OctoReaction.STATE_STARTED);

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

						meta.SetReactionState(reaction.GetID(), OctoReaction.STATE_EXECUTED);

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
					meta.SetReactionState(reaction.GetID(), OctoReaction.STATE_NONE);

					StopNotify(OctoReaction.DELAY_PREFIX
						+ reaction.GetID());
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					meta.SetReactionState(reaction.GetID(), OctoReaction.STATE_NONE);
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
		return meta.GetAttribute(valid_const).GetBoolean();
	}

	@Override
	public void SetValid()
	{
		meta.SetAttribute(valid_const, true);
	}

	@Override
	public void SetInvalid()
	{
		meta.SetAttribute(valid_const, false);
	}
}
