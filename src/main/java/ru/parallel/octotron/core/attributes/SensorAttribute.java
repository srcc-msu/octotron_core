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
	private final long update_time;

	private boolean is_missing; // automatic
	private boolean is_valid = true; // user defined

	public SensorAttribute(ModelEntity parent, String name
		, Object value, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name, value);
		this.update_time = update_time;

		SetCTime(JavaUtils.GetTimestamp());
		SetIsMissing(false);
	}

	public SensorAttribute(ModelEntity parent, String name, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name, null);

		if(update_time == -1)
			throw new ExceptionModelFail("update time = never, default value must be specified");
		this.update_time = update_time;

		SetIsMissing(true);
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
		SetIsMissing(false);

		super.Update(new_value);
	}

	public boolean UpdateIsMissing(long cur_time)
	{
		if(update_time == -1)
			return false;

		SetIsMissing(cur_time - GetCTime() > update_time);

		return IsMissing();
	}

	public boolean IsMissing()
	{
		return is_missing;
	}

	@Override
	public boolean Check()
	{
		if(GetCTime() == 0)
			throw new ExceptionModelFail("must not enter here");

		return is_valid && !is_missing;
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

		result.put("is_valid", IsValid());
		result.put("is_missing", IsMissing());

		result.put("update_time", update_time);

		return result;
	}
}
