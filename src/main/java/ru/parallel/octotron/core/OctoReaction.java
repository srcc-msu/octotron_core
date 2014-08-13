/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.impl.PersistentStorage;

import java.io.Serializable;

public class OctoReaction implements Serializable, UniqueName
{
	private static final long serialVersionUID = 8900268116120488911L;

	private long reaction_id;

	private final String check_name;
	private final Object check_value;

	private final OctoResponse response;
	private final OctoResponse recover_response;

	private final long delay;
	private final long repeat;

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, long delay, long repeat, OctoResponse recover_response)
	{
		this.check_name = check_name;
		this.check_value = check_value;

		this.delay = delay;
		this.repeat = repeat;

		this.response = response;
		this.recover_response = recover_response;

		Register();
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, OctoResponse recover_response)
	{
		this(check_name, check_value, response, 0, 0, recover_response);
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, long delay, long repeat)
	{
		this(check_name, check_value, response, delay, repeat, null);
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response)
	{
		this(check_name, check_value, response, 0, 0, null);
	}

	private void Register()
	{
		reaction_id = PersistentStorage.INSTANCE.GetReactions().Add(this);
	}

	public long GetID()
	{
		return reaction_id;
	}

	public Object GetCheckValue()
	{
		return check_value;
	}

	public OctoResponse GetResponse()
	{
		return response;
	}

	public OctoResponse GetRecoverResponse()
	{
		return recover_response;
	}

	public long GetDelay()
	{
		return delay;
	}

	public String GetCheckName()
	{
		return check_name;
	}

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

	public static final String DELAY_PREFIX = "_DELAY_";

/**
 * reaction is needed if the \check_name attribute is false
 * */
	public boolean ReactionNeeded(ModelEntity entity)
	{
		ModelAttribute attr = entity.GetAttribute(check_name);

		if(!attr.IsValid())
			return false;

		return attr.eq(check_value);
	}

	@Override
	public String GetUniqName()
	{
		return GetCheckName();
	}
}
