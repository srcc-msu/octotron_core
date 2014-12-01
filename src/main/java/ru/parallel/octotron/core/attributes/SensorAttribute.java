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
	private boolean is_missing = false;

	/**
	 * user defined
	 * */
	private boolean is_valid = true;

	/**
	 * tracks if the sensor got at least one value update:
	 * initially or via Update()
	 * */
	private boolean got_initial_value = false;

	public SensorAttribute(ModelEntity parent, String name
		, Object value, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name, value);
		this.update_time = update_time;

		SetCTime(JavaUtils.GetTimestamp());
		got_initial_value = true;
	}

	public SensorAttribute(ModelEntity parent, String name, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name, null);

		if(update_time == -1)
			throw new ExceptionModelFail("update time is set to never, default sensor value must be specified");
		this.update_time = update_time;

		got_initial_value = false;
	}

	@Override
	public SensorAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new SensorAttributeBuilder(service, this);
	}

	private void SetIsMissing(boolean value)
	{
		is_missing = value;
	}

	@Override
	public void Update(Object new_value)
	{
		got_initial_value = true;
		SetIsMissing(false);

		super.Update(new_value);
	}

	// TODO is it ok? unclear
	private static long TOLERANCE = 30; // 30 seconds tolerance for sensor update

	public boolean UpdateIsMissing(long cur_time)
	{
		if(update_time == -1)
			return false;

		SetIsMissing(cur_time - GetCTime() > update_time + TOLERANCE);

		return IsMissing();
	}

	public boolean IsMissing()
	{
		return is_missing;
	}

	public boolean GotInitialValue()
	{
		return got_initial_value;
	}

	@Override
	public boolean Check()
	{
		return IsValid() && !IsMissing();
	}

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

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("got_initial_value", GotInitialValue());
		result.put("is_valid", IsValid());
		result.put("is_missing", IsMissing());

		result.put("update_time", update_time);

		return result;
	}
}
