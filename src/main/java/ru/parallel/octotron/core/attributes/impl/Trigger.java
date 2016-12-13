/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.attributes.EAttributeType;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

/**
 * Trigger is a special attribute, that uses boolean rules to track
 * relationships between other attributes.
 * Trigger tracks how many times condition persists and how long it lasts.
 * */
public class Trigger extends Attribute
{
	private final Rule condition;

	private long repeat = 0;
	private long started = 0;

	public Trigger(ModelEntity parent, String name, Rule condition)
	{
		super(EAttributeType.TRIGGER, parent, name, new Value(false), 0);
		this.condition = condition;
	}

	public Rule GetCondition()
	{
		return condition;
	}

	public void ForceTrigger(long current_time)
	{
		Update(new Value(true), current_time);
	}

/**
 * check if condition is met or not and update
 * the state and statistics accordingly
 * */
	@Override
	public void UpdateSelf(long current_time)
	{
		boolean state = IsTriggered();

		Value value = Value.Construct(condition.Compute(GetParent(), this));

		boolean condition_met = value.IsValid() && value.GetBoolean();

		if(!state && condition_met) // first match
		{
			repeat = 1;
			started = current_time;

			Update(new Value(true), current_time);
		}
		else if(state && condition_met) // next match
		{
			repeat += 1;
		}
		else if(state && !condition_met) // matched before
		{
			repeat = 0;
			started = 0;

			Update(new Value(false), current_time);
		}

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterTrigger(this);
	}

	public boolean IsTriggered()
	{
		return GetBoolean();
	}

	public long GetRepeat()
	{
		return repeat;
	}

	public long GetDelay(long current_time)
	{
		if(started == 0)
			return 0;
		else
			return current_time - started;
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
