/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
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

/**
 * Reaction attributes hold an action to execute when specified conditions are met.
 * Reaction may be dynamically turned on and off through suppression mechanism.
 * */
public class Reaction extends Attribute
{
	private final ReactionAction template;

	/**
	 * tracks how many times the reaction repeats
	 * */
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

/**
 * check if reaction must be executed in current context and executes it
 * */
	@Override
	protected void UpdateSelf()
	{
		boolean last_state = IsExecuted();

		if(ReactionNeeded(GetParent()))
		{
			if(last_state == false || last_state == true && template.repeatable)
			{
				UpdateValue(new Value(true));

				IncCounter();

				if(template.response != null)
					RegisterResponse(template.response);
			}
		}
		else
		{
			if(last_state == true)
			{
				UpdateValue(new Value(false));

				if(template.recover_response != null) // was true, now false -> recover
					RegisterResponse(template.recover_response);

				RegisterPreparedResponse(null); // unregister executed reaction
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

/**
 * check that all required triggered are turned on
 * and all prohibited triggers are turned off
 * */
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
		result.put("is_suppressed", is_suppressed);
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
