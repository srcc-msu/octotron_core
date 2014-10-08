package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.JavaUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Reaction extends UniqueID<EEntityType>
{
	private final ReactionTemplate template;
	private final ModelEntity parent;

	private Map<Long, Marker> markers;

	private long state;
	private long stat;

	private long delay;
	private long repeat;

	public static final long STATE_NONE = 0;
	public static final long STATE_STARTED = 1;
	public static final long STATE_EXECUTED = 2;

	public Reaction(ReactionTemplate template, ModelEntity parent)
	{
		super(EEntityType.REACTION);

		this.template = template;
		this.parent = parent;

		markers = new HashMap<>();

		state = 0;
		stat = 0;
	}

	public ReactionTemplate GetTemplate()
	{
		return template;
	}

	public long GetState()
	{
		return state;
	}

	public void SetState(long new_state)
	{
		state = new_state;
	}

	public long AddMarker(String descr, boolean suppress)
	{
		Marker m = new Marker(this, descr, suppress);

		markers.put(m.GetID(), m);
		return m.GetID();
	}

	public Collection<Marker> GetMarkers()
	{
		return markers.values();
	}

	public void DeleteMarker(long id)
	{
		markers.remove(id);
	}

	@Nullable
	public Response Process()
	{
		boolean needed = template.ReactionNeeded(parent);

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

					return template.GetResponse();
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

					return template.GetResponse();
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
					return template.GetResponse();
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
					return template.GetRecoverResponse();
			}
		}

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

// -------------

	private void IncStat()
	{
		stat++;
	}

	public long GetStat()
	{
		return stat;
	}

	public void ResetStat()
	{
		stat = 0;
	}
}
