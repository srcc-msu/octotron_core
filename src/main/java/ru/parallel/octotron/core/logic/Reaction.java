/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.IPresentable;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.JavaUtils;

import java.util.HashMap;
import java.util.Map;

public class Reaction extends UniqueID<EEntityType> implements IPresentable
{
	private final ReactionTemplate template;
	private final IModelAttribute attribute;

	private long delay = 0;
	private long repeat = 0;

	private long state = 0;
	private long global_stat = 0;

	private String descr = "";
	private boolean suppress = false;
	private PreparedResponse prepared_response = null;

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

	public Reaction(ReactionTemplate template, IModelAttribute attribute)
	{
		super(EEntityType.REACTION);

		this.template = template;
		this.attribute = attribute;
	}

	public ReactionTemplate GetTemplate()
	{
		return template;
	}
	public IModelAttribute GetAttribute()
	{
		return attribute;
	}

// -------------

	public long GetState()
	{
		return state;
	}

	public void SetState(long new_state)
	{
		state = new_state;
	}

// -------------

	private void IncGlobalStat()
	{
		global_stat++;
	}

	public long GetGlobalStat()
	{
		return global_stat;
	}

	public void SetGlobalStat(long stat)
	{
		this.global_stat = stat;
	}

	public void ResetGlobalStat()
	{
		global_stat = 0;
	}

// -------------

	public void SetDescription(String descr)
	{
		this.descr = descr;
	}

	public void SetSuppress(boolean suppress)
	{
		this.suppress = suppress;
	}

	public String GetDescription()
	{
		return descr;
	}

	public boolean GetSuppress()
	{
		return suppress;
	}

// -------------

	public Response Process()
	{
		boolean needed = template.ReactionNeeded(attribute);

		Response result = null;

		if(needed)
		{
			boolean ready = (delay >= template.GetDelay())
				&& (repeat >= template.GetRepeat());

			if(state == STATE_NONE)
			{
				if(ready)
				{
					IncGlobalStat();
					SetState(STATE_EXECUTED);

					result = template.GetResponse();
				}
				else
				{
					SetState(STATE_STARTED);

					StartDelay();
				}
			}
			else if(state == STATE_STARTED)
			{
				if(ready)
				{
					IncGlobalStat();
					SetState(STATE_EXECUTED);

					result =  template.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
			else if(state == STATE_EXECUTED)
			{
				if(template.IsRepeatable())
				{
					IncGlobalStat();
					result = template.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
		}
		else
		{
			if(state == STATE_NONE)
			{
				// nothing to see here
			}
			else if(state == STATE_STARTED)
			{
				SetState(STATE_NONE);
				DropDelay();
				DropRepeat();
			}
			else if(state == STATE_EXECUTED)
			{
				SetState(STATE_NONE);
				DropDelay();
				DropRepeat();

				if(template.GetRecoverResponse() != null)
					result = template.GetRecoverResponse();
			}
		}

		if(result != null)
			return result.Suppress(suppress);

		return null;
	}


	private void StartDelay()
	{
		delay = JavaUtils.GetTimestamp();
	}

	public long GetDelay()
	{
		if(delay == 0) // timer not started
			return 0;

		return JavaUtils.GetTimestamp() - delay;
	}

	private void DropDelay()
	{
		delay = 0;
	}

// -------------

	public void Repeat(Object new_value)
	{
		if(new_value.equals(template.GetCheckValue()))
			repeat++;
		else
			DropRepeat();
	}

	public long GetRepeat()
	{
		return repeat;
	}

	private void DropRepeat()
	{
		repeat = 0;
	}

	public void RegisterPreparedResponse(PreparedResponse new_prepared_response)
	{
		prepared_response = new_prepared_response;
	}

	public PreparedResponse GetPreparedResponse()
	{
		return prepared_response;
	}

	public Map<String, Object> GetRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		Map<String, Object> reaction_map = new HashMap<>();
		result.put("info", reaction_map);

		reaction_map.put("AID", GetID());

		reaction_map.put("delay", GetDelay());
		reaction_map.put("repeat", GetRepeat());

		reaction_map.put("state", GetState());
		reaction_map.put("global_stat", GetGlobalStat());

		reaction_map.put("descr", GetDescription());
		reaction_map.put("suppressed", GetSuppress());

		result.put("template", GetTemplate().GetRepresentation());

		Map<String, Object> model_map = new HashMap<>();
		result.put("model", model_map);

		Map<String, Object> attribute_map = new HashMap<>();
		model_map.put("attribute", attribute_map);

		attribute_map.put("AID", attribute.GetID());
		attribute_map.put("name", attribute.GetName());
		attribute_map.put("value", attribute.GetValue());

		Map<String, Object> entity_map = new HashMap<>();
		model_map.put("entity", entity_map);

		entity_map.put("AID", attribute.GetParent().GetID());

		result.put("usr", GetTemplate().GetResponse().GetMessages());
		return result;
	}

}
