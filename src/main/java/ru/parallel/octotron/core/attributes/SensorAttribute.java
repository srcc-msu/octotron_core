/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;

import java.util.HashMap;
import java.util.Map;

public class SensorAttribute extends AbstractModAttribute
{
	private final Object default_value;
	private final long update_time;

	private boolean is_missing = true; // automatic
	private boolean is_valid = true; // user defined

	public SensorAttribute(ModelEntity parent, String name, Object default_value, long update_time)
	{
		super(EAttributeType.SENSOR, parent, name, default_value);
		this.default_value = default_value;
		this.update_time = update_time;
	}

	@Override
	public SensorAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new SensorAttributeBuilder(service, this);
	}

	@Override
	public Object GetValue()
	{
		if(Check())
			return super.GetValue();
		return default_value;
	}

	private void SetIsMissing(boolean value)
	{
		is_missing = value;
	}

	public boolean Update(Object new_value)
	{
		SetIsMissing(false);

		return super.Update(new_value);
	}

	public boolean UpdateIsMissing(long cur_time)
	{
		if(update_time == -1)
			return true;

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

	public void SetValid(boolean is_valid)
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
		Map<String, Object> result = new HashMap<>();

		result.put("is_valid", IsValid());
		result.put("is_missing", IsMissing());

		result.put("default_value", default_value);
		result.put("update_time", update_time);

		return result;
	}
}
