/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import main.java.ru.parallel.octotron.primitive.EAttributeValueType;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.utils.JavaUtils;

/**
 * implementation of attribute<br>
 * stores a value retrieved from the graph<br>
 * can be used to set a new value<br>
 * implements {@link OctoAttribute} interface<br>
 * */
public class OctoAttribute extends SimpleAttribute
{
	private EAttributeValueType value_type;
	private long change_time;

	private static Long IToL(Object value)
	{
		return Long.valueOf(((Integer) value).longValue());
	}

	OctoAttribute(String name, Object value, long change_time, OctoEntity parent)
		throws ExceptionModelFail
	{
		super(name, value, parent);

		this.change_time = change_time;

		if(value instanceof Integer)
		{
			value_type = EAttributeValueType.LONG;
			this.value = IToL(value);
		}
		else if(value instanceof Long)
			value_type = EAttributeValueType.LONG;
		else if(value instanceof Double)
			value_type = EAttributeValueType.DOUBLE;
		else if(value instanceof String)
			value_type = EAttributeValueType.STRING;
		else if(value instanceof Boolean)
			value_type = EAttributeValueType.BOOLEAN;
		else
			throw new ExceptionModelFail("unsupported value type for attribute: "
				+ name + "[" + value.getClass() + "]");
	}

	public void Commit()
		throws ExceptionModelFail, ExceptionDBError
	{
		GraphService graph_service = parent.GetGraph();

		graph_service.SetAttribute(parent, name, value, change_time);
	}

	public void SetValue(Object new_value)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(new_value instanceof Integer)
			new_value = IToL(new_value);

		if(value.getClass() != new_value.getClass())
			throw new ExceptionModelFail(name + " : new value type for attribute mismatch");

		value = new_value;
		change_time = JavaUtils.GetTimestamp();

		Commit();
	}

	public Boolean Update(Object new_value)
		throws ExceptionModelFail, ExceptionDBError
	{
		if(ne(new_value) || GetTime() == 0)
		{
			SetValue(new_value);
			return true;
		}

		return false;
	}

	public EAttributeValueType GetValueType()
	{
		return value_type;
	}

	public long GetTime()
	{
		return change_time;
	}

	public boolean IsValid()
		throws ExceptionModelFail
	{
		return parent.GetGraph().IsValid(parent, name);
	}

	public void SetValid(boolean value)
		throws ExceptionModelFail, ExceptionDBError
	{
		parent.GetGraph().SetValid(parent, name, value);
	}

	public final String GetString()
		throws ExceptionModelFail
	{
		if(value_type != EAttributeValueType.STRING)
			throw new ExceptionModelFail("trying to get String from " + name + " that has type " + value_type);

		return (String) GetValue();
	}

	public final Long GetLong()
		throws ExceptionModelFail
	{
		if(value_type != EAttributeValueType.LONG)
			throw new ExceptionModelFail("trying to get Long from " + name + " that has type " + value_type);

		return (Long) GetValue();
	}

	public final Double GetDouble()
		throws ExceptionModelFail
	{
		if(value_type != EAttributeValueType.DOUBLE)
			throw new ExceptionModelFail("trying to get Dobule from " + name + " that has type " + value_type);

		return (Double) GetValue();
	}

	public final Boolean GetBoolean()
		throws ExceptionModelFail
	{
		if(value_type != EAttributeValueType.BOOLEAN)
			throw new ExceptionModelFail("trying to get Boolean from " + name + " that has type " + value_type);

		return (Boolean) GetValue();
	}

	public Double ToDouble()
		throws ExceptionModelFail
	{
		switch(value_type)
		{
			case DOUBLE:
				return GetDouble();
			case LONG:
				return GetLong().doubleValue();
			case STRING:
				throw new ExceptionModelFail("bad value type for casting to Double");
			case BOOLEAN:
				throw new ExceptionModelFail("bad value type for casting to Double");
			default:
				throw new ExceptionModelFail("bad value type for casting to Double");
		}
	}

	public final boolean eq(Object new_value)
		throws ExceptionModelFail
	{
		if(new_value instanceof Integer)
			new_value = IToL(new_value);

		if(value.getClass() != new_value.getClass())
			throw new ExceptionModelFail(
				"comparing values types mismatch: "
					+ name + "[" + value_type + "] and "
					+ new_value + "["+ new_value.getClass() + "]");

		return value.equals(new_value);
	}

	public final boolean aeq(Object new_value, Object aprx)
		throws ExceptionModelFail
	{
		if(new_value instanceof Integer)
			new_value = IToL(new_value);

		if(value.getClass() != new_value.getClass())
			throw new ExceptionModelFail(
				"comparing values types mismatch: "
					+ name + "[" + value_type + "] and "
					+ new_value + "["+ new_value.getClass() + "]");

		switch(value_type)
		{
			case DOUBLE:
				return GetDouble() > (Double)new_value - (Double)aprx
					&& GetDouble() < (Double)new_value + (Double)aprx;
			case LONG:
				return GetLong() > (Long)new_value - (Long)aprx
					&& GetLong() < (Long)new_value + (Long)aprx;
			case STRING:
				throw new ExceptionModelFail("bad value type for comparison");
			case BOOLEAN:
				throw new ExceptionModelFail("bad value type for comparison");
			default:
				throw new ExceptionModelFail("bad value type for comparison");
		}
	}

	public final boolean ne(Object new_value)
		throws ExceptionModelFail
	{
		return !eq(new_value);
	}

	public final boolean gt(Object new_value)
		throws ExceptionModelFail
	{
		if(new_value instanceof Integer)
			new_value = IToL(new_value);

		try
		{
			switch(value_type)
			{
				case DOUBLE:
					return GetDouble() > (Double) new_value;
				case LONG:
					return GetLong() > (Long) new_value;
				case STRING:
					throw new ExceptionModelFail("bad value type for comparison");
				case BOOLEAN:
					throw new ExceptionModelFail("bad value type for comparison");
				default:
					throw new ExceptionModelFail("bad value type for comparison");
			}
		}
		catch(ClassCastException e)
		{
			throw new ExceptionModelFail(
				"comparing values types mismatch: "
					+ name + "[" + value_type + "] and "
					+ new_value + "["+ new_value.getClass() + "]");
		}
	}

	public final boolean lt(Object new_value)
		throws ExceptionModelFail
	{
		if(new_value instanceof Integer)
			new_value = IToL(new_value);

		try
		{
			switch(value_type)
			{
				case DOUBLE:
					return GetDouble() < (Double) new_value;
				case LONG:
					return GetLong() < (Long) new_value;
				case STRING:
					throw new ExceptionModelFail("bad value type for comparison");
				case BOOLEAN:
					throw new ExceptionModelFail("bad value type for comparison");
				default:
					throw new ExceptionModelFail("bad value type for comparison");
			}
		}
		catch(ClassCastException e)
		{
			throw new ExceptionModelFail(
					"comparing values types mismatch: "
						+ name + "[" + value_type + "] and "
						+ new_value + "["+ new_value.getClass() + "]");
		}
	}

	public final boolean ge(Object val)
		throws ExceptionModelFail
	{
		return !lt(val);
	}

	public final boolean le(Object val)
		throws ExceptionModelFail
	{
		return !gt(val);
	}
}
