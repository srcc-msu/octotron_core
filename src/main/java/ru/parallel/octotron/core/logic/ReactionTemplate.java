/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.io.Serializable;

public abstract class ReactionTemplate implements Serializable
{
	private final String check_name;
	private final Object check_value;

	private Response response = null;
	private Response recover_response = null;

	private long wait_delay = 0;
	private long wait_repeat = 0;
	private boolean repeatable = false;

	public ReactionTemplate(String check_name, Object check_value)
	{
		this.check_name = check_name;
		this.check_value = SimpleAttribute.ConformType(check_value);
	}

	public final Object GetCheckValue()
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
}
