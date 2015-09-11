/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

public final class Sensor extends Attribute
{
	/**
	 * time when the sensor must get a new value
	 * */
	private final long update_interval;

	/**
	 * automatically updated, when sensor timeout reached
	 * */
	private boolean is_outdated = false;

	/**
	 * user defined
	 * */
	private boolean is_user_valid = true;

	Reaction timeout_reaction = null;

	public Sensor(ModelEntity parent, String name, long update_interval
		, Value value)
	{
		super(EAttributeType.SENSOR, parent, name, value);

		if(update_interval == -1 && !value.IsDefined())
			throw new ExceptionModelFail("update time is set to never, default sensor value must be specified");

		this.update_interval = update_interval;

		SetCTime(JavaUtils.GetTimestamp());
	}

//--------

	// TODO is it ok? unclear
	private static final long TOLERANCE = 30; // 30 seconds tolerance for sensor update

	@Override
	public void UpdateSelf()
	{
		if(update_interval == -1)
			return;

		SetIsOutdated(JavaUtils.GetTimestamp() - GetCTime() > update_interval + TOLERANCE);

		if(IsOutdated()) // if timeout - turns to simply invalid
			UpdateValue(Value.invalid);

		UpdateDependant();

		// TODO: add timeout reaction?
	}

	public boolean IsOutdated()
	{
		return is_outdated;
	}

	private void SetIsOutdated(boolean value)
	{
		is_outdated = value;
	}

//--------

	@Override
	public Value GetValue()
	{
		if(IsUserValid() && !IsOutdated())
			return super.GetValue();

		return Value.invalid;
	}

	@Override
	public boolean IsValid()
	{
		if(!IsUserValid())
			return false;

		return super.IsValid();
	}

	@Override
	public void UpdateValue(Value new_value)
	{
		SetIsOutdated(false);

		super.UpdateValue(new_value);
	}

	public void UpdateValue(Object new_value)
	{
		UpdateValue(Value.Construct(new_value));
	}

//--------

	public void SetUserValid()
	{
		is_user_valid = true;
	}

	public void SetUserInvalid()
	{
		is_user_valid = false;
	}

	public void SetIsUserValid(boolean is_valid)
	{
		this.is_user_valid = is_valid;
	}

	public boolean IsUserValid()
	{
		return is_user_valid;
	}

//--------

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("ctime", GetCTime());
		result.put("is_user_valid", is_user_valid);
		result.put("is_outdated", is_outdated);
		result.put("update_interval", update_interval);

		return result;
	}

	public Reaction GetTimeoutReaction()
	{
		return timeout_reaction;
	}
}
