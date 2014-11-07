/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.ELogicalType;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.JavaUtils;

import java.util.HashMap;
import java.util.Map;

public class Reaction extends LogicID<ELogicalType> implements IPresentable
{
	public enum State
	{
		NONE, STARTED, EXECUTED
	}

	private State state = State.NONE;

	private final ReactionTemplate template;
	private final IModelAttribute attribute;

	private long delay = 0;
	private long repeat = 0;

	private long global_stat = 0;

	private String descr = "";
	private boolean suppress = false;

	private PreparedResponse prepared_response = null;

	public Reaction(ReactionTemplate template, IModelAttribute attribute)
	{
		super(ELogicalType.REACTION);

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

	public State GetState()
	{
		return state;
	}

	public void SetState(State new_state)
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

			if(state == State.NONE)
			{
				if(ready)
				{
					IncGlobalStat();
					SetState(State.EXECUTED);

					result = template.GetResponse();
				}
				else
				{
					SetState(State.STARTED);

					StartDelay();
				}
			}
			else if(state == State.STARTED)
			{
				if(ready)
				{
					IncGlobalStat();
					SetState(State.EXECUTED);

					result =  template.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
			else if(state == State.EXECUTED)
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
			if(state == State.NONE)
			{
				// nothing to see here
			}
			else if(state == State.STARTED)
			{
				SetState(State.NONE);
				DropDelay();
				DropRepeat();
			}
			else if(state == State.EXECUTED)
			{
				SetState(State.NONE);
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

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetID());
		result.put("state", GetState().toString());

		result.put("template", GetTemplate().GetID());
		result.put("attribute", attribute.GetID());

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = GetShortRepresentation();

		result.put("delay", GetDelay());
		result.put("repeat", GetRepeat());

		result.put("global_stat", GetGlobalStat());

		result.put("descr", GetDescription());
		result.put("suppressed", GetSuppress());

		return result;
	}
}
