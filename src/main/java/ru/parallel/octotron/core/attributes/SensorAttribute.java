/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.impl.Timeout;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
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
	private boolean is_user_valid = true;

	Reaction timeout_reaction = null;

	public SensorAttribute(ModelEntity parent, String name, long update_time
		, Value value)
	{
		super(EAttributeType.SENSOR, parent, name, value);

		if(update_time == -1 && !value.IsDefined())
			throw new ExceptionModelFail("update time is set to never, default sensor value must be specified");

		this.update_time = update_time;

		SetCTime(JavaUtils.GetTimestamp());

		try
		{
			timeout_reaction = new Reaction(
				new Timeout(GetName())
					.Response(new Response(EEventStatus.INFO)
						.Msg("tag", "TIMEOUT")
						.Msg("descr", "sensor value has not been updated in required time")
						.Msg("loc", "AID = {AID}")
						.Msg("msg", "sensor(" + GetName() + ") value has not been updated in required time")
						.Exec("on_info"))
				, this);
		}
		catch (ExceptionParseError exceptionParseError)
		{
			throw new ExceptionModelFail("internal error in builtin reaction description");
		}
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
			SetValue(Value.invalid);

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
	public Value GetValue()
	{
		if(IsUserValid() && !IsOutdated())
			return super.GetValue();

		return Value.invalid;
	}

// ---------------------------

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

// ---------------------------

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("is_user_valid", IsUserValid());
		result.put("is_missing", IsOutdated());

		result.put("update_time", update_time);

		return result;
	}

	public Reaction GetTimeoutReaction()
	{
		return timeout_reaction;
	}
}
