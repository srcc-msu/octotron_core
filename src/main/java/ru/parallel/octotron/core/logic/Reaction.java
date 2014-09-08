/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.storage.PersistentStorage;

import java.io.Serializable;

public class Reaction implements Serializable, UniqueName
{
	private static final long serialVersionUID = 8900268116120488911L;

	private long reaction_id;

	private final String check_name;
	private final Object check_value;

	private final Response response;
	private final Response recover_response;

	private final long delay;
	private final long repeat;

	public Reaction(String check_name, Object check_value
		, Response response, long delay, long repeat, Response recover_response)
	{
		this.check_name = check_name;
		this.check_value = check_value;

		this.delay = delay;
		this.repeat = repeat;

		this.response = response;
		this.recover_response = recover_response;

		Register();
	}

	public Reaction(String check_name, Object check_value
		, Response response, Response recover_response)
	{
		this(check_name, check_value, response, 0, 0, recover_response);
	}

	public Reaction(String check_name, Object check_value
		, Response response, long delay, long repeat)
	{
		this(check_name, check_value, response, delay, repeat, null);
	}

	public Reaction(String check_name, Object check_value
		, Response response)
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

	public Response GetResponse()
	{
		return response;
	}

	public Response GetRecoverResponse()
	{
		return recover_response;
	}

	public long GetDelay()
	{
		return delay;
	}

	public long GetRepeat()
	{
		return repeat;
	}

	public String GetCheckName()
	{
		return check_name;
	}

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

/**
 * reaction is needed if the \check_name attribute is false
 * */
	public boolean ReactionNeeded(ModelEntity entity)
	{
		IMetaAttribute attr = entity.GetMetaAttribute(check_name);

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
