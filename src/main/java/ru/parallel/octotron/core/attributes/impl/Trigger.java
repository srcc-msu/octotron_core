package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

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

	public void ForceTrigger()
	{
		UpdateValue(new Value(true));
	}

	@Override
	public void UpdateSelf()
	{
		boolean state = IsTriggered();

		Value value = Value.Construct(condition.Compute(GetParent(), this));

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

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();

		result.put("repeat", repeat);
		result.put("started", started);

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("repeat", repeat);
		result.put("started", started);

		return result;
	}
}
