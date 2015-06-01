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

	private long global_stat = 0;

	private String descr = "";
	private boolean suppressed = false;

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
				IncGlobalStat();

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

	public void SetSuppressed(boolean suppressed)
	{
		this.suppressed = suppressed;
	}

	public String GetDescription()
	{
		return descr;
	}

	public boolean GetSuppressed()
	{
		return suppressed;
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

/*		// now it matches

		if(!IsExecuted() || IsExecuted() && template.repeatable)
			return true;

		return false; // executed and not repeatable*/
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

		result.put("suppressed", suppressed);
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
