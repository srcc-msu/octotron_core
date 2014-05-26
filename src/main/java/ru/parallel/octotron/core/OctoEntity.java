/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.impl.PersistenStorage;
import ru.parallel.octotron.logic.TimerProcessor;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoAttributeList;
import ru.parallel.octotron.utils.SimpleAttributeList;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * some entity, that resides in model<br>
 * all operations with it go through the \graph interface, no caching<br>
 * */
public abstract class OctoEntity
{
	protected static final String RULE_PREFIX = "_rule";
	private static final String REACTION_PREFIX = "_reaction";
	private static final String REACTION_EXECUTED_PREFIX = "_reaction_executed";
	private static final String MARKER_PREFIX = "_marker";
	/**
 * graph that stores this entity<br>
 * */
	protected final GraphService graph_service;

/**
 * unique identifier of the entity<br>
 * needed to access it from the \graph<br>
 * */
	private final Uid uid;

/**
 * this constructor MUST not be used for creating new items -<br>
 * it is needed to obtain the existing from the \graph<br>
 * */
	OctoEntity(GraphService graph_service, Uid uid)
	{
		this.graph_service = graph_service;
		this.uid = uid;
	}

	public final Uid GetUID()
	{
		return uid;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof OctoEntity))
			return false;

		return uid.getUid() == ((OctoEntity)object).GetUID().getUid();
	}

	public OctoAttribute GetAttribute(String name)
	{
		return graph_service.GetAttribute(this, name);
	}

	public OctoAttributeList GetAttributes()
	{
		return graph_service.GetAttributes(this);
	}

	public SimpleAttributeList GetMetaAttributes()
	{
		return graph_service.GetAllMeta(this);
	}

	public OctoAttribute DeclareAttribute(String name, Object value)
	{
		if(!GraphService.IsStaticName(name) && TestAttribute(name))
			throw new ExceptionModelFail("attribute " + name + " already declared");

		return graph_service.SetAttribute(this, name, SimpleAttribute.ConformType(value));
	}

	public OctoAttribute DeclareAttribute(SimpleAttribute att)
	{
		return DeclareAttribute(att.GetName(), att.GetValue());
	}

	public void DeclareAttributes(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute att : attributes)
		{
			DeclareAttribute(att);
		}
	}

	public void Delete()
	{
		graph_service.Delete(this);
	}

	public void RemoveAttribute(String name)
	{
		graph_service.DeleteAttribute(this, name);
	}

	public boolean TestAttribute(String name)
	{
		return graph_service.TestAttribute(this, name);
	}

	public abstract long Update(EDependencyType dep);

	public List<OctoRule> GetRules()
	{
		List<OctoRule> rules = new LinkedList<>();

		for(long id : graph_service.GetArray(this, OctoEntity.RULE_PREFIX))
			rules.add(PersistenStorage.INSTANCE.GetRules().Get(id));

		return rules;
	}

	public List<OctoReaction> GetReactions()
	{
		List<OctoReaction> reactions = new LinkedList<>();

		for(long id : graph_service.GetArray(this, OctoEntity.REACTION_PREFIX))
			reactions.add(PersistenStorage.INSTANCE.GetReactions().Get(id));

		return reactions;
	}

	public List<Marker> GetMarkers()
	{
		List<Marker> markers = new LinkedList<>();

		for(long id : graph_service.GetArray(this, OctoEntity.MARKER_PREFIX))
			markers.add(PersistenStorage.INSTANCE.GetMarkers().Get(id));

		return markers;
	}

	public void AddRule(OctoRule rule)
	{
		graph_service.AddToArray(this, OctoEntity.RULE_PREFIX, rule.GetID());

		DeclareAttribute(rule.GetAttr(), rule.GetDefaultValue());
	}

	public void AddRules(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
			AddRule(rule);
	}

	public void AddReaction(OctoReaction reaction)
	{
		graph_service.AddToArray(this, OctoEntity.REACTION_PREFIX, reaction.GetID());
		graph_service.AddToArray(this, OctoEntity.REACTION_EXECUTED_PREFIX, 0L);
	}

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
			AddReaction(reaction);
	}

	public void SetTimer(String name, long expires)
	{
		if(TestAttribute(name))
			graph_service.DeleteAttribute(this, name);

		OctoAttribute attr = DeclareAttribute(name, 0);
		attr.Update(expires, true);

		TimerProcessor.AddTimer(attr);
	}

	// TODO move reactions to meta syntax
	public List<OctoResponse> PreparePendingReactions()
	{
		List<Marker> markers = GetMarkers();

		List<OctoResponse> result = new LinkedList<>();

		long current_time = JavaUtils.GetTimestamp();

		for(OctoReaction reaction : GetReactions())
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

			boolean needed = reaction.ReactionNeeded(this);
			long state = GetReactionState(reaction.GetID());

			if(needed && skip_marker != null)
			{
				System.err.println("reaction suppressed: " + skip_marker.GetDescription()
					+ " marker id: " + skip_marker.GetID()
					+ " target id:" + skip_marker.GetTarget()
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

						SetReactionState(reaction.GetID(), OctoReaction.STATE_EXECUTED);
					}
					else
					{
						SetReactionState(reaction.GetID(), OctoReaction.STATE_STARTED);

						SetTimer(OctoReaction.DELAY_PREFIX
							+ reaction.GetCheckName(), delay + 1);
					}
				}
				else if(state == OctoReaction.STATE_STARTED)
				{
					long delay = reaction.GetDelay();
					long event_time = this.GetAttribute(OctoReaction.DELAY_PREFIX + reaction.GetCheckName()).GetCTime();

					if(current_time - event_time > delay)
					{
						result.add(reaction.GetResponse());

						SetReactionState(reaction.GetID(), OctoReaction.STATE_EXECUTED);
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
					SetReactionState(reaction.GetID(), OctoReaction.STATE_NONE);
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					SetReactionState(reaction.GetID(), OctoReaction.STATE_NONE);
				}
			}
		}

		return result;
	}

	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> fails = new LinkedList<>();

		for(OctoReaction reaction : GetReactions())
		{
			boolean needed = reaction.ReactionNeeded(this);

			if(needed)
				fails.add(reaction.GetResponse());
		}

		return fails;
	}

	public long GetReactionState(long key)
	{
		List<Long> id = graph_service.GetArray(this, OctoEntity.REACTION_PREFIX);
		List<Long> executed = graph_service.GetArray(this, OctoEntity.REACTION_EXECUTED_PREFIX);

		return executed.get(id.indexOf(key));
	}

	public void SetReactionState(long key, long res)
	{
		List<Long> id = graph_service.GetArray(this, OctoEntity.REACTION_PREFIX);
		List<Long> executed = graph_service.GetArray(this, OctoEntity.REACTION_EXECUTED_PREFIX);

		int index = id.indexOf(key);
		executed.set(index, res);
		graph_service.SetArray(this, OctoEntity.REACTION_EXECUTED_PREFIX, executed);
	}

	public long AddMarker(long reaction_id, String description, boolean suppress)
	{
		long AID = GetAttribute("AID").GetLong();
		Marker marker = new Marker(AID, reaction_id, description, suppress);
		graph_service.AddToArray(this, OctoEntity.MARKER_PREFIX, marker.GetID());
		return marker.GetID();
	}

	public void DeleteMarker(long marker_id)
	{
		List<Long> markers_id = graph_service.GetArray(this, OctoEntity.MARKER_PREFIX);

		if(!markers_id.contains(marker_id))
			throw new ExceptionModelFail("can not delete: marker with id " + marker_id + " not found");

		markers_id.remove(marker_id); // NOW it removes by value, not by index...

		graph_service.SetArray(this, OctoEntity.MARKER_PREFIX, markers_id);

		PersistenStorage.INSTANCE.GetMarkers().Delete(marker_id);
	}
}
