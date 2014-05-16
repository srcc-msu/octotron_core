/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import java.util.LinkedList;
import java.util.List;

import main.java.ru.parallel.octotron.impl.PersistenStorage;
import main.java.ru.parallel.octotron.logic.TimerProcessor;
import main.java.ru.parallel.octotron.neo4j.impl.Marker;
import main.java.ru.parallel.octotron.primitive.EDependencyType;
import main.java.ru.parallel.octotron.primitive.Uid;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.utils.LinkList;
import main.java.ru.parallel.octotron.utils.ObjectList;
import main.java.ru.parallel.utils.JavaUtils;

/**
 * implementation of object according to real \graph<br>
 * implements {@link OctoObject} interface<br>
 * */
public class OctoObject extends OctoEntity
{
	private static final String RULE_PREFIX = "_rule";
	private static final String REACTION_PREFIX = "_reaction";
	private static final String REACTION_EXECUTED_PREFIX = "_reaction_executed";
	private static final String MARKER_PREFIX = "_marker";

	/**
	 * this constructor MUST not be used manually for -<br>
	 * creating new items -<br>
	 * it is needed to obtain the existing from the \graph<br>
	 * */
	OctoObject(GraphService graph, Uid uid)
	{
		super(graph, uid);
	}

	public LinkList GetInLinks()
	{
		return graph_service.GetInLinks(this);
	}

	public LinkList GetOutLinks()
	{
		return graph_service.GetOutLink(this);
	}

	public ObjectList GetInNeighbors(String link_name
		, Object link_value)
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetInLinks().Filter(link_name, link_value))
			objects.add(link.Source());

		return objects;
	}

	public ObjectList GetOutNeighbors(String link_name
		, Object link_value)
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetOutLinks().Filter(link_name, link_value))
			objects.add(link.Target());

		return objects;
	}

	public ObjectList GetInNeighbors(String link_name)
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetInLinks().Filter(link_name))
			objects.add(link.Source());

		return objects;
	}

	public ObjectList GetOutNeighbors(String link_name)
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetOutLinks().Filter(link_name))
			objects.add(link.Target());

		return objects;
	}

	public ObjectList GetInNeighbors()
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetInLinks())
			objects.add(link.Source());

		return objects;
	}

	public ObjectList GetOutNeighbors()
	{
		ObjectList objects = new ObjectList();

		for(OctoLink link : GetOutLinks())
			objects.add(link.Target());

		return objects;
	}

	public List<OctoRule> GetRules()
	{
		List<OctoRule> rules = new LinkedList<OctoRule>();

		for(long id : graph_service.GetArray(this, OctoObject.RULE_PREFIX))
			rules.add(PersistenStorage.INSTANCE.GetRules().Get(id));

		return rules;
	}

	public List<OctoReaction> GetReactions()
	{
		List<OctoReaction> reactions = new LinkedList<OctoReaction>();

		for(long id : graph_service.GetArray(this, OctoObject.REACTION_PREFIX))
			reactions.add(PersistenStorage.INSTANCE.GetReactions().Get(id));

		return reactions;
	}

	public List<Marker> GetMarkers()
	{
		List<Marker> markers = new LinkedList<Marker>();

		for(long id : graph_service.GetArray(this, OctoObject.MARKER_PREFIX))
			markers.add(PersistenStorage.INSTANCE.GetMarkers().Get(id));

		return markers;
	}

	public void AddRule(OctoRule rule)
	{
		graph_service.AddToArray(this, OctoObject.RULE_PREFIX, rule.GetID());

		DeclareAttribute(rule.GetAttr(), rule.GetDefaultValue());
	}

	public void AddRules(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
			AddRule(rule);
	}

	public void AddReaction(OctoReaction reaction)
	{
		graph_service.AddToArray(this, OctoObject.REACTION_PREFIX, reaction.GetID());
		graph_service.AddToArray(this, OctoObject.REACTION_EXECUTED_PREFIX, 0L);
	}

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
			AddReaction(reaction);
	}

	public void SetTimer(String name, long expires)
	{
		OctoAttribute attr = DeclareAttribute(name, expires);

		TimerProcessor.AddTimer(attr);
	}

	public boolean Update(EDependencyType dep)
	{
		boolean changed = false;

		List<Long> keys = graph_service.GetArray(this, OctoObject.RULE_PREFIX);

		for(long key : keys)
		{
			OctoRule rule = PersistenStorage.INSTANCE.GetRules().Get(key);

			if(rule.GetDeps() != EDependencyType.ALL && rule.GetDeps() != dep)
				continue;

			Object new_val = rule.Compute(this);

			if(GetAttribute(rule.GetAttr()).Update(new_val))
				changed = true;
		}

		return changed;
	}

	public List<OctoResponse> PreparePendingReactions()
	{
		List<Marker> markers = GetMarkers();

		List<OctoResponse> result = new LinkedList<OctoResponse>();

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
			long state = IsReactionExecuted(reaction.GetID());

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

						SetReactionExecuted(reaction.GetID(), OctoReaction.STATE_EXECUTED);
					}
					else
					{
						SetReactionExecuted(reaction.GetID(), OctoReaction.STATE_STARTED);

						SetTimer(OctoReaction.DELAY_PREFIX
							+ reaction.GetCheckName(), delay + 1);
					}
				}
				else if(state == OctoReaction.STATE_STARTED)
				{
					long delay = reaction.GetDelay();
					long event_time = this.GetAttribute(OctoReaction.DELAY_PREFIX + reaction.GetCheckName()).GetTime();

					if(current_time - event_time > delay)
					{
						result.add(reaction.GetResponse());

						SetReactionExecuted(reaction.GetID(), OctoReaction.STATE_EXECUTED);
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
					SetReactionExecuted(reaction.GetID(), OctoReaction.STATE_NONE);
				}
				else if(state == OctoReaction.STATE_EXECUTED)
				{
					if(reaction.GetRecoverResponse() != null)
						result.add(reaction.GetRecoverResponse());

					SetReactionExecuted(reaction.GetID(), OctoReaction.STATE_NONE);
				}
			}
		}

		return result;
	}

	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> fails = new LinkedList<OctoResponse>();

		for(OctoReaction reaction : GetReactions())
		{
			boolean needed = reaction.ReactionNeeded(this);

			if(needed)
				fails.add(reaction.GetResponse());
		}

		return fails;
	}

	public long IsReactionExecuted(long key)
	{
		List<Long> id = graph_service.GetArray(this, OctoObject.REACTION_PREFIX);
		List<Long> executed = graph_service.GetArray(this, OctoObject.REACTION_EXECUTED_PREFIX);

		return executed.get(id.indexOf(key));
	}

	public void SetReactionExecuted(long key, long res)
	{
		List<Long> id = graph_service.GetArray(this, OctoObject.REACTION_PREFIX);
		List<Long> executed = graph_service.GetArray(this, OctoObject.REACTION_EXECUTED_PREFIX);

		int index = id.indexOf(key);
		executed.set(index, res);
		graph_service.SetArray(this, OctoObject.REACTION_EXECUTED_PREFIX, executed);
	}

	public long AddMarker(long reaction_id, String description, boolean suppress)
	{
		long AID = GetAttribute("AID").GetLong();
		Marker marker = new Marker(AID, reaction_id, description, suppress);
		graph_service.AddToArray(this, OctoObject.MARKER_PREFIX, marker.GetID());
		return marker.GetID();
	}

	public void DelMarker(long marker_id)
	{
		List<Long> markers_id = graph_service.GetArray(this, OctoObject.MARKER_PREFIX);

		if(!markers_id.contains(marker_id))
			throw new ExceptionModelFail("can not delete: marker with id " + marker_id + " not found");

		markers_id.remove(marker_id); // NOW it removes by value, not by index...

		graph_service.SetArray(this, OctoObject.MARKER_PREFIX, markers_id);

		PersistenStorage.INSTANCE.GetMarkers().Delete(marker_id);
	}
}
