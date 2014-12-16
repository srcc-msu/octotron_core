/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.logic.LogicID;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.ELogicalType;
import ru.parallel.octotron.core.primitive.IPresentable;

import java.util.HashMap;
import java.util.Map;

public abstract class ReactionTemplate extends LogicID<ELogicalType> implements IPresentable
{
	private final String check_name;

	private Response response = null;
	private Response recover_response = null;

	private long wait_delay = 0;
	private long wait_repeat = 0;
	private boolean repeatable = false;
	private boolean invalid_allowed = false;

	public ReactionTemplate(String check_name)
	{
		super(ELogicalType.REACTION_TEMPLATE);

		this.check_name = check_name;
	}

	public final String GetCheckName()
	{
		return check_name;
	}

//----------------

	public abstract boolean ReactionNeeded(IModelAttribute attribute);

//----------------

	public final Response GetResponseOrNull()
	{
		return response;
	}

	public final Response GetRecoverResponseOrNull()
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

	public boolean IsInvalidAllowed()
	{
		return invalid_allowed;
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

	public ReactionTemplate InvalidAllowed()
	{
		this.invalid_allowed = true;
		return this;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetID());
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
