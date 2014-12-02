/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

public final class SensorAttribute extends AbstractModAttribute
{
	/**
	 * time when the sensor must get a new value
	 * */
	private final long update_time;

	/**
	 * automatically updated, when sensor timeout reached
	 * */
	private boolean is_outdated = false;

	/**
	 * user defined
	 * */
	private boolean is_valid = true;

	public SensorAttribute(ModelEntity parent, String name, long update_time
		, Value value)
	{
		super(EAttributeType.SENSOR, parent, name, value);
		this.update_time = update_time;

		SetCTime(JavaUtils.GetTimestamp());
	}

	public SensorAttribute(ModelEntity parent, String name, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name);

		if(update_time == -1)
			throw new ExceptionModelFail("update time is set to never, default sensor value must be specified");
		this.update_time = update_time;
	}

	@Override
	public SensorAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new SensorAttributeBuilder(service, this);
	}

// ---------------------------

	// TODO is it ok? unclear
	private static final long TOLERANCE = 30; // 30 seconds tolerance for sensor update

	public boolean IsOutdated()
	{
		return is_outdated;
	}

	private void SetIsOutdated(boolean value)
	{
		is_outdated = value;
	}

	public boolean UpdateIsOutdated(long cur_time)
	{
		if(update_time == -1)
			return false;

		SetIsOutdated(cur_time - GetCTime() > update_time + TOLERANCE);

		if(IsOutdated()) // if timeout - turns to simply invalid
			CancelInitialDelay();

		return IsOutdated();
	}

// ---------------------------

	@Override
	public void Update(Value new_value)
	{
		SetIsOutdated(false);

		super.Update(new_value);
	}

	public void Update(Object new_value)
	{
		Update(Value.Construct(new_value));
	}

	@Override
	public boolean Check()
	{
		return IsValid() && !IsOutdated() && !IsInitialDelay();
	}

// ---------------------------

	public void SetValid()
	{
		is_valid = true;
	}

	public void SetInvalid()
	{
		is_valid = false;
	}

	public void SetIsValid(boolean is_valid)
	{
		this.is_valid = is_valid;
	}

	public boolean IsValid()
	{
		return is_valid;
	}

// ---------------------------

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("is_valid", IsValid());
		result.put("is_missing", IsOutdated());

		result.put("update_time", update_time);

		return result;
	}
}
