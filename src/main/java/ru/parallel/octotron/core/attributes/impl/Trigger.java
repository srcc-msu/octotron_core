package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

public class Trigger extends Attribute
{
	private final Rule condition;

	private long repeat = 0;
	private long started = 0;

	public Trigger(ModelEntity parent, String name, Rule condition)
	{
		super(EAttributeType.TRIGGER, parent, name, new Value(false));
		this.condition = condition;
	}

	public Rule GetCondition()
	{
		return condition;
	}

	public synchronized void ForceTrigger()
	{
		UpdateValue(new Value(true));
	}

	@Override
	public synchronized void UpdateSelf()
	{
		boolean state = IsTriggered();

		Value value = Value.Construct(condition.Compute(GetParent()));

		boolean condition_met = value.IsValid() && value.GetBoolean();

		if(!state && condition_met) // first match
		{
			repeat = 1;
			started = JavaUtils.GetTimestamp();

			UpdateValue(new Value(true));
		}
		else if(state && condition_met) // next match
		{
			repeat += 1;
		}
		else if(state && !condition_met) // matched before
		{
			repeat = 0;
			started = 0;

			UpdateValue(new Value(false));
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
