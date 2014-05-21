/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.utils.JavaUtils;

/**
 * implementation of attribute<br>
 * stores a value retrieved from the graph<br>
 * can be used to set a new value<br>
 * does not distinguish int/long and float/double<br>
 * */
public class OctoAttribute extends SimpleAttribute
{
	public static final double DELTA = 0.001;

	private final GraphService graph_service;
	private long ctime = 0;
	private long atime = 0;

	OctoAttribute(GraphService graph_service, OctoEntity parent, String name, Object value)
	{
		super(name, value, parent);

		this.graph_service = graph_service;

		if(graph_service.TestMeta(parent, name, OctoAttribute.CTIME))
			ctime = (Long)graph_service.GetMeta(parent, name, OctoAttribute.CTIME);

		if(graph_service.TestMeta(parent, name, OctoAttribute.ATIME))
			atime = (Long)graph_service.GetMeta(parent, name, OctoAttribute.ATIME);
	}

	private void CheckTypes(Object a_value)
	{
		if(!value.getClass().equals(a_value.getClass()))
		{
			String error = String.format("mismatch types: %s=%s[%s] and %s[%s]"
				, name, value, value.getClass().getName(), a_value, a_value.getClass().getName());

			throw new ExceptionModelFail(error);
		}
	}

	private void CheckType(Class<?> check_class)
	{
		if(!value.getClass().equals(check_class))
		{
			String error = String.format("mismatch types: %s=%s[%s] and [%s]"
				, name, value, value.getClass().getName(), check_class.getName());

			throw new ExceptionModelFail(error);
		}
	}

	private static final String ATIME = "atime";
	private static final String CTIME = "ctime";
	private static final String LAST_VAL = "last_val";
	private static final String LAST_CTIME = "last_ctime";

	public long GetCTime()
	{
		return ctime;
	}

	public long GetATime()
	{
		return atime;
	}

	public double GetSpeed()
	{
		if(!graph_service.TestMeta(parent, name, OctoAttribute.LAST_VAL))
			return 0.0;
		if(!graph_service.TestMeta(parent, name, OctoAttribute.LAST_CTIME))
			return 0.0;

		Object last_val = graph_service.GetMeta(parent, name, OctoAttribute.LAST_VAL);
		long last_time = (Long)graph_service.GetMeta(parent, name, OctoAttribute.LAST_CTIME);

		if(ctime - last_time == 0)
			return 0.0;

		if(last_time == 0)
			return 0.0;

		Class<?> my_class = value.getClass();

		if(my_class.equals(Double.class))
		{
			Double diff = (Double)value - (Double)last_val;
			return diff / (ctime - last_time);
		}
		else if(my_class.equals(Long.class))
		{
			Long diff = (Long)value - (Long)last_val;
			return diff.doubleValue() / (ctime - last_time);
		}
		else
			throw new ExceptionModelFail("bad value type type for approximate comparison");
	}

	private void RotateValue(Object new_value, long cur_time)
	{
		graph_service.SetMeta(parent, name, OctoAttribute.LAST_VAL, value);
		graph_service.SetMeta(parent, name, OctoAttribute.LAST_CTIME, GetCTime());

		value = new_value;
		ctime = cur_time;

		graph_service.SetAttribute(parent, name, value);
		graph_service.SetMeta(parent, name, OctoAttribute.CTIME, cur_time);
	}

	private void Touch(long cur_time)
	{
		graph_service.SetMeta(parent, name, OctoAttribute.ATIME, cur_time);
		atime = cur_time;
	}

/**
 * update the value if it has changed or has not been initialized</br>
 * if allow_overwrite is true - update the time even if the value is the same</br>
 * the flag is needed to prevent rule attribute chains from updating every second</br>
 * that would be correct, but too slow</br>
 * TODO: rethink
 * */
	public Boolean Update(Object new_value, boolean allow_overwrite)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckTypes(new_value);

		long cur_time = JavaUtils.GetTimestamp();
		Touch(cur_time);

// if got a new value, or was not initialized or allow_overwrite is on
		if(ne(new_value) || GetCTime() == 0 || allow_overwrite)
		{
			RotateValue(new_value, cur_time);
			return true;
		}

		return false;
	}
	public final String GetString()
	{
		CheckType(String.class);

		return (String) GetValue();
	}

	public final Long GetLong()
	{
		CheckType(Long.class);

		return (Long) GetValue();
	}

	public final Double GetDouble()
	{
		CheckType(Double.class);

		return (Double) GetValue();
	}

	public final Boolean GetBoolean()
	{
		CheckType(Boolean.class);

		return (Boolean) GetValue();
	}

	public Double ToDouble()
	{
		Class<?> my_class = value.getClass(); 

		if(my_class.equals(Double.class))
			return GetDouble();
		else if(my_class.equals(Long.class))
			return GetLong().doubleValue();
		else
			throw new ExceptionModelFail("bad value type for casting to Double");
	}

	public final boolean eq(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckTypes(new_value);

		return value.equals(new_value);
	}

	public final boolean aeq(Object new_value, Object aprx)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class.equals(Double.class))
			return GetDouble() > (Double)new_value - (Double)aprx
				&& GetDouble() < (Double)new_value + (Double)aprx;
		else if(my_class.equals(Long.class))
			return GetLong() > (Long)new_value - (Long)aprx
				&& GetLong() < (Long)new_value + (Long)aprx;
		else
			throw new ExceptionModelFail("bad value type type for approximate comparison");
	}

	public final boolean ne(Object new_value)
	{
		return !eq(new_value);
	}

	public final boolean gt(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class.equals(Double.class))
			return GetDouble() > (Double)new_value;
		else if(my_class.equals(Long.class))
			return GetLong() > (Long)new_value;
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	public final boolean lt(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class.equals(Double.class))
			return GetDouble() < (Double)new_value;
		else if(my_class.equals(Long.class))
			return GetLong() < (Long)new_value;
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	public final boolean ge(Object val)
	{
		return !lt(val);
	}

	public final boolean le(Object val)
	{
		return !gt(val);
	}

//-------------------
//	      INVALID
//-------------------

	private static final String INVALID_KEY = "invalid";

	public boolean IsValid()
	{
		return !graph_service.TestMeta(parent, name, OctoAttribute.INVALID_KEY);
	}

	public void SetValid()
	{
		if(graph_service.TestMeta(parent, name, OctoAttribute.INVALID_KEY))
			graph_service.DeleteMeta(parent, name, OctoAttribute.INVALID_KEY);
	}

	public void SetInvalid()
	{
		graph_service.SetMeta(parent, name, OctoAttribute.INVALID_KEY, true);
	}
}
