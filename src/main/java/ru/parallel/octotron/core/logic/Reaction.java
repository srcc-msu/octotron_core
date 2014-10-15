/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.utils.JavaUtils;

import javax.annotation.Nullable;

public class Reaction extends UniqueID<EEntityType>
{
	private final ReactionTemplate template;
	private final IModelAttribute attribute;

	private long delay = 0;
	private long repeat = 0;

	private long state = 0;
	private long stat = 0;

	private String descr = "";
	private boolean suppress = false;

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

	public Reaction(ReactionTemplate template, IModelAttribute attribute)
	{
		super(EEntityType.REACTION);

		this.template = template;
		this.attribute = attribute;
	}

	public ReactionTemplate GetTemplate()
	{
		return template;
	}
	public IModelAttribute GetAttribute()
	{
		return attribute;
	}

// -------------

	public long GetState()
	{
		return state;
	}

	public void SetState(long new_state)
	{
		state = new_state;
	}

// -------------

	private void IncStat()
	{
		stat++;
	}

	public long GetStat()
	{
		return stat;
	}

	public void SetStat(long stat)
	{
		this.stat = stat;
	}

	public void ResetStat()
	{
		stat = 0;
	}

// -------------

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

// -------------

	@Nullable
	public Response Process()
	{
		boolean needed = template.ReactionNeeded(attribute);

		Response result = null;

		if(needed)
		{
			boolean ready = (delay >= template.GetDelay())
				&& (repeat >= template.GetRepeat());

			if(state == STATE_NONE)
			{
				if(ready)
				{
					IncStat();
					SetState(STATE_EXECUTED);

					result = template.GetResponse();
				}
				else
				{
					SetState(STATE_STARTED);

					StartDelay();
				}
			}
			else if(state == STATE_STARTED)
			{
				if(ready)
				{
					IncStat();
					SetState(STATE_EXECUTED);

					result =  template.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
			else if(state == STATE_EXECUTED)
			{
				if(template.IsRepeatable())
				{
					IncStat();
					result = template.GetResponse();
				}
				else
				{
					// nothing to see here
				}
			}
		}
		else
		{
			if(state == STATE_NONE)
			{
				// nothing to see here
			}
			else if(state == STATE_STARTED)
			{
				SetState(STATE_NONE);
				DropDelay();
				DropRepeat();
			}
			else if(state == STATE_EXECUTED)
			{
				SetState(STATE_NONE);
				DropDelay();
				DropRepeat();

				if(template.GetRecoverResponse() != null)
					result = template.GetRecoverResponse();
			}
		}

		if(result != null)
			return result.Suppress(suppress);
		return null;
	}


	private void StartDelay()
	{
		delay = JavaUtils.GetTimestamp();
	}

	public long GetDelay()
	{
		if(delay == 0) // timer not started
			return 0;

		return JavaUtils.GetTimestamp() - delay;
	}

	private void DropDelay()
	{
		delay = 0;
	}

// -------------

	public void Repeat(Object new_value)
	{
		if(new_value.equals(template.GetCheckValue()))
			repeat++;
		else
			DropRepeat();
	}

	public long GetRepeat()
	{
		return repeat;
	}

	private void DropRepeat()
	{
		repeat = 0;
	}
}
