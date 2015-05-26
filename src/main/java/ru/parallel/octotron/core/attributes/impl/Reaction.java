/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.generators.tmpl.ReactionAction;
import ru.parallel.octotron.generators.tmpl.ReactionCase;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.Map;

public class Reaction extends Attribute
{
	private final ReactionAction template;

	private long global_stat = 0;

	private String descr = "";
	private boolean suppress = false;

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
	protected synchronized void AutoUpdate(boolean silent)
	{
		boolean state = IsExecuted();

		if(ReactionNeeded(GetParent()))
		{
			IncGlobalStat();

			if(!silent && template.response != null)
				RegisterResponse(template.response);

			Update(new Value(true));
		}
		else
		{
			if(state && template.recover_response != null) // was true, now false -> recover
			{
				if(!silent)
					RegisterResponse(template.recover_response);
			}

			Update(new Value(false));
		}
	}

//--------

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

//--------

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

		// now it matches

		if(!IsExecuted() || IsExecuted() && template.repeatable)
			return true;

		return false; // executed and not repeatable
	}

	public void RegisterResponse(Response response)
	{
		return; // TODO
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
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();

		result.put("name", GetName());
		result.put("suppress", suppress);

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("name", GetName());
		result.put("suppress", suppress);
		result.put("descr", descr);
		result.put("global_stat", GetGlobalStat());

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
