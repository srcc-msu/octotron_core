/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.utils.JavaUtils;

/**
 * implementation of attribute<br>
 * stores a value retrieved from the graph<br>
 * can be used to set a new value<br>
 * does not distinguish int/long and float/double<br>
 * */
public class OctoAttribute extends SimpleAttribute
{
	private GraphService graph_service;
	private long change_time;

	OctoAttribute(GraphService graph_service, OctoEntity parent, String name, Object value)
	{
		super(name, value, parent);

		this.graph_service = graph_service;

		change_time = 0;

		if(graph_service.TestMeta(parent, name, TIME_PREFIX))
			change_time = (Long)graph_service.GetMeta(parent, name, TIME_PREFIX);
	}

	private void CheckTypes(Object a_value)
	{
		if(value.getClass() != a_value.getClass())
		{
			String error = String.format("mismatch types: %s=%s[%s] and %s[%s]"
				, name, value, value.getClass().getName(), a_value, a_value.getClass().getName());

			throw new ExceptionModelFail(error);
		}
	}

	private void CheckType(Class<?> check_class)
	{
		if(value.getClass() != check_class)
		{
			String error = String.format("mismatch types: %s=%s[%s] and [%s]"
				, name, value, value.getClass().getName(), check_class.getName());

			throw new ExceptionModelFail(error);
		}
	}

	private static final String TIME_PREFIX = "time";
	private static final String LAST_PREFIX = "last";
	private static final String LASTTIME_PREFIX = "lasttime";

	public long GetTime()
	{
		return change_time;
	}

	public double GetSpeed()
	{
		if(!graph_service.TestMeta(parent, name, LAST_PREFIX))
			return 0.0;
		if(!graph_service.TestMeta(parent, name, LASTTIME_PREFIX))
			return 0.0;

		Object last_val = graph_service.GetMeta(parent, name, LAST_PREFIX); 
		long last_time = (Long)graph_service.GetMeta(parent, name, LASTTIME_PREFIX);

		Class<?> my_class = value.getClass(); 

		if(my_class == Double.class)
		{
			Double diff = (Double)value - (Double)last_val;
			return diff / (change_time - last_time);
		}
		else if(my_class == Long.class)
		{
			Long diff = (Long)value - (Long)last_val;
			return diff.doubleValue() / (change_time - last_time);
		}
		else
			throw new ExceptionModelFail("bad value type type for approximate comparison");
	}

	public Boolean Update(Object new_value)
	{
		new_value = ConformType(new_value);
		CheckTypes(new_value);

		if(ne(new_value) || GetTime() == 0)
		{
			graph_service.SetMeta(parent, name, LAST_PREFIX, value);
			graph_service.SetMeta(parent, name, LASTTIME_PREFIX, GetTime());

			value = new_value;

			graph_service.SetAttribute(parent, name, value);
			graph_service.SetMeta(parent, name, TIME_PREFIX, JavaUtils.GetTimestamp());

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

		if(my_class == Double.class)
			return GetDouble();
		else if(my_class == Long.class)
			return GetLong().doubleValue();
		else
			throw new ExceptionModelFail("bad value type for casting to Double");
	}

	public final boolean eq(Object new_value)
	{
		new_value = ConformType(new_value);
		CheckTypes(new_value);

		return value.equals(new_value);
	}

	public final boolean aeq(Object new_value, Object aprx)
	{
		new_value = ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class == Double.class)
			return GetDouble() > (Double)new_value - (Double)aprx
				&& GetDouble() < (Double)new_value + (Double)aprx;
		else if(my_class == Long.class)
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
		new_value = ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class == Double.class)
			return GetDouble() > (Double)new_value;
		else if(my_class == Long.class)
			return GetLong() > (Long)new_value;
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	public final boolean lt(Object new_value)
	{
		new_value = ConformType(new_value);
		CheckTypes(new_value);

		Class<?> my_class = value.getClass(); 

		if(my_class == Double.class)
			return GetDouble() < (Double)new_value;
		else if(my_class == Long.class)
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
		return graph_service.TestMeta(parent, name, INVALID_KEY);
	}

	public void SetValid()
	{
		if(graph_service.TestMeta(parent, name, INVALID_KEY))
			graph_service.DeleteMeta(parent, name, INVALID_KEY);
	}

	public void SetInvalid()
	{
		graph_service.SetMeta(parent, name, INVALID_KEY, true);
	}
}
