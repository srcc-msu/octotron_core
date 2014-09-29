/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.UniqueName;
import ru.parallel.octotron.storage.PersistentStorage;

import java.io.Serializable;

public abstract class Reaction implements Serializable, UniqueName
{
	private static final long serialVersionUID = 8900268116120488911L;

	private long reaction_id;

	private final String check_name;
	private final Object check_value;

	private Response response = null;
	private Response recover_response = null;

	private long wait_delay = 0;
	private long wait_repeat = 0;
	private boolean repeatable = false;

	public Reaction(String check_name, Object check_value)
	{
		this.check_name = check_name;
		this.check_value = check_value;

		Register();
	}

	private void Register()
	{
		reaction_id = PersistentStorage.INSTANCE.GetReactions().Add(this);
	}

	@Override
	public String GetUniqName()
	{
		return GetCheckName();
	}

//----------------

	public final long GetID()
	{
		return reaction_id;
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

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

	public abstract boolean ReactionNeeded(ModelEntity entity);

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

	public Reaction Response(Response response)
	{
		this.response = response;
		return this;
	}

	public Reaction RecoverResponse(Response recover_response)
	{
		this.recover_response = recover_response;
		return this;
	}

	public Reaction Delay(long wait_delay)
	{
		this.wait_delay = wait_delay;
		return this;
	}

	public Reaction Repeat(long wait_repeat)
	{
		this.wait_repeat = wait_repeat;
		return this;
	}

	public Reaction Repeatable(boolean repeatable)
	{
		this.repeatable = repeatable;
		return this;
	}
}