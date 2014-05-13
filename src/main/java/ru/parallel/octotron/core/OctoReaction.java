/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import main.java.ru.parallel.octotron.impl.PersistenStorage;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class OctoReaction implements java.io.Serializable
{
	private static final long serialVersionUID = 8900268116120488911L;

	private long reaction_id;

	private String check_name;
	private Object check_value;

	private OctoResponse response;
	private OctoResponse recover_response;

	private long delay;

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, long delay, OctoResponse recover_response)
			throws ExceptionModelFail
	{
		this.check_name = check_name;
		this.check_value = check_value;

		this.delay = delay;

		this.response = response;
		this.recover_response = recover_response;

		Register();
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, OctoResponse recover_response)
			throws ExceptionModelFail
	{
		this(check_name, check_value, response, 0, recover_response);
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response, long delay)
			throws ExceptionModelFail
	{
		this(check_name, check_value, response, delay, null);
	}

	public OctoReaction(String check_name, Object check_value
		, OctoResponse response)
			throws ExceptionModelFail
	{
		this(check_name, check_value, response, 0, null);
	}

	private void Register()
		throws ExceptionModelFail
	{
		reaction_id = PersistenStorage.INSTANCE.GetReactions().Add(this);
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

	public static final int STATE_NONE = 0;
	public static final int STATE_STARTED = 1;
	public static final int STATE_EXECUTED = 2;

	public static final String DELAY_PREFIX = "_DELAY_";

/**
 * reaction is needed if the \check_name attribute is false
 * */
	public boolean ReactionNeeded(OctoEntity entity)
		throws ExceptionModelFail
	{
		OctoAttribute attr = entity.GetAttribute(check_name);

		if(attr.GetTime() <= 0 || !attr.IsValid())
			return false;

		return attr.eq(check_value);
	}
}
