/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.generators.tmpl.ReactionCase;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.Map;

public class Reaction extends Attribute
{
	private final ReactionAction template;

	private long counter = 0;

	private String description = "";
	private boolean is_suppressed = false;

	private PreparedResponse prepared_response = null;

	public Reaction(ModelEntity parent, String name, ReactionAction template)
	{
		super(EAttributeType.REACTION, parent, name, new Value(false));

		this.template = template;
	}

	public ReactionAction GetTemplate()
	{
		return template;
	}

//--------

	public boolean IsExecuted()
	{
		return GetBoolean();
	}

	@Override
	protected synchronized void UpdateSelf()
	{
		boolean last_state = IsExecuted();

		if(ReactionNeeded(GetParent()))
		{
			if(last_state == false || last_state == true && template.repeatable)
			{
				IncCounter();

				if (template.response != null)
					RegisterResponse(template.response);

				UpdateValue(new Value(true));
			}
		}
		else
		{
			if(last_state == true && template.recover_response != null) // was true, now false -> recover
			{
				RegisterResponse(template.recover_response);
				UpdateValue(new Value(false));
			}
		}
	}

//--------

	private void IncCounter()
	{
		counter++;
	}

	public long GetCounter()
	{
		return counter;
	}

	public void SetCounter(long counter)
	{
		this.counter = counter;
	}

//--------

	public void SetDescription(String descr)
	{
		this.description = descr;
	}

	public void SetSuppressed(boolean suppressed)
	{
		this.is_suppressed = suppressed;
	}

	public String GetDescription()
	{
		return description;
	}

	public boolean IsSuppressed()
	{
		return is_suppressed;
	}

//--------

	private boolean ReactionNeeded(ModelEntity entity)
	{
		for(ReactionCase required : template.required_triggers)
		{
			if(!required.Match(entity))
				return false;
		}

		for(ReactionCase prohibited : template.prohibited_triggers)
		{
			if(prohibited.Match(entity))
				return false;
		}

		return true;
	}

	public void RegisterResponse(Response response)
	{
		ServiceLocator.INSTANCE.GetReactionService().AddResponse(this, response);
	}

	public void RegisterPreparedResponse(PreparedResponse new_prepared_response)
	{
		prepared_response = new_prepared_response;
	}

	public PreparedResponse GetPreparedResponseOrNull()
	{
		return prepared_response;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("ctime", GetCTime());
		result.put("suppressed", is_suppressed);
		result.put("description", description);
		result.put("counter", counter);

		return result;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}
}
