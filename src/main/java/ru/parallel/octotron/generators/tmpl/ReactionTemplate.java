/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.logic.LogicID;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.ELogicalType;
import ru.parallel.octotron.core.primitive.IPresentable;

import java.util.HashMap;
import java.util.Map;

public abstract class ReactionTemplate extends LogicID<ELogicalType> implements IPresentable
{
	private final String check_name;
	private final Value check_value;

	private Response response = null;
	private Response recover_response = null;

	private long wait_delay = 0;
	private long wait_repeat = 0;
	private boolean repeatable = false;

	public ReactionTemplate(String check_name, Object check_value)
	{
		super(ELogicalType.REACTION_TEMPLATE);

		this.check_name = check_name;
		this.check_value = Value.Construct(check_value);
	}

	public final Value GetCheckValue()
	{
		return check_value;
	}

	public final String GetCheckName()
	{
		return check_name;
	}

//----------------

	public abstract boolean ReactionNeeded(IModelAttribute attribute);

//----------------

	public final Response GetResponse()
	{
		return response;
	}

	public final Response GetRecoverResponse()
	{
		return recover_response;
	}

	public long GetDelay()
	{
		return wait_delay;
	}

	public long GetRepeat()
	{
		return wait_repeat;
	}

	public boolean IsRepeatable()
	{
		return repeatable;
	}

	public ReactionTemplate Response(Response response)
	{
		this.response = response;
		return this;
	}

	public ReactionTemplate RecoverResponse(Response recover_response)
	{
		this.recover_response = recover_response;
		return this;
	}

	public ReactionTemplate Delay(long wait_delay)
	{
		this.wait_delay = wait_delay;
		return this;
	}

	public ReactionTemplate Repeat(long wait_repeat)
	{
		this.wait_repeat = wait_repeat;
		return this;
	}

	public ReactionTemplate Repeatable()
	{
		this.repeatable = true;
		return this;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetID());

		result.put("check_value", GetCheckValue());
		result.put("check_name", GetCheckName());

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = GetShortRepresentation();

		result.put("wait_delay", GetDelay());
		result.put("wait_repeat", GetRepeat());
		result.put("repeatable", IsRepeatable());

		result.put("response", response.GetID());

		if(recover_response != null)
			result.put("recover_response", recover_response.GetID());
		else
			result.put("recover_response", -1);

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
