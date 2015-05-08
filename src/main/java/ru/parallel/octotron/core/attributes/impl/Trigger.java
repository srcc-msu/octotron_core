package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.logic.TriggerCondition;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

public class Trigger extends Attribute
{
	private final TriggerCondition condition;

	private long repeat = 0;
	private long started = 0;

	public Trigger(ModelEntity parent, String name, TriggerCondition condition)
	{
		super(EAttributeType.TRIGGER, parent, name, new Value(false));
		this.condition = condition;
	}

	public synchronized void ForceTrigger()
	{
		Update(new Value(true));
		UpdateDependant();
	}

	@Override
	public synchronized void AutoUpdate(boolean silent)
	{
		boolean state = IsTriggered();

		boolean condition_met = condition.Check(GetParent());

		if(!state && condition_met) // first match
		{
			repeat = 1;
			started = JavaUtils.GetTimestamp();

			Update(new Value(true));
		}
		else if(state && condition_met) // next match
		{
			repeat += 1;
		}
		else if(state && !condition_met) // matched before
		{
			repeat = 0;
			started = 0;

			Update(new Value(false));
		}
	}

	public boolean IsTriggered()
	{
		return GetBoolean();
	}

	public long GetRepeat()
	{
		return repeat;
	}

	public long GetDelay()
	{
		if(started == 0)
			return 0;
		else
			return JavaUtils.GetTimestamp() - started;
	}
}
