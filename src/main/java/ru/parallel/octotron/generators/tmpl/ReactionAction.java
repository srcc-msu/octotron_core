/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.IPresentable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ReactionAction implements IPresentable
{
	public final List<ReactionCase> required_triggers = new LinkedList<>();
	public final List<ReactionCase> prohibited_triggers = new LinkedList<>();

	public Response response = null;
	public Response recover_response = null;

	public boolean repeatable = false;

//--------

	public boolean IsRepeatable()
	{
		return repeatable;
	}

	public ReactionAction Begin(Response response)
	{
		this.response = response;
		return this;
	}

	public ReactionAction End(Response recover_response)
	{
		this.recover_response = recover_response;
		return this;
	}

	public ReactionAction Repeatable()
	{
		this.repeatable = true;
		return this;
	}

	public ReactionAction On(String trigger_name, long repeat, long delay)
	{
		this.required_triggers.add(new ReactionCase(trigger_name, repeat, delay));
		return this;
	}

	public ReactionAction On(String trigger_name)
	{
		return On(trigger_name, 0, 0);
	}

	public ReactionAction Off(String trigger_name)
	{
		this.prohibited_triggers.add(new ReactionCase(trigger_name));
		return this;
	}

/*
	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetInfo());

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = GetShortRepresentation();

		result.put("wait_delay", GetDelay());
		result.put("wait_repeat", GetRepeat());
		result.put("repeatable", IsRepeatable());

		result.put("response", response.GetInfo());

		if(recover_response != null)
			result.put("recover_response", recover_response.GetInfo());
		else
			result.put("recover_response", -1);

		return result;
	}*/

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		return null;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		return null;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}

	private List<String> triggers_names_cache = null;

	public Collection<String> GetTriggerNames()
	{
		if(triggers_names_cache == null)
		{
			triggers_names_cache = new LinkedList<>();

			for (ReactionCase reaction_case : required_triggers)
				triggers_names_cache.add(reaction_case.trigger_name);

			for (ReactionCase reaction_case : prohibited_triggers)
				triggers_names_cache.add(reaction_case.trigger_name);
		}

		return triggers_names_cache;
	}
}
