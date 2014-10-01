/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

/**
 * implementation of attribute<br>
 * stores a value retrieved from the graph<br>
 * can be used to set a new value<br>
 * does not distinguish int/long and float/double<br>
 * */
public class GraphAttribute extends SimpleAttribute implements IAttribute
{
	public static final double EPSILON = 0.00001;

	protected final GraphEntity parent;

	public GraphAttribute(GraphEntity parent, String name)
	{
		super(name, null);

		this.parent = parent;
	}

	public GraphEntity GetParent()
	{
		return parent;
	}

	@Override
	public Object GetValue()
	{
		return parent.GetRawAttribute(name);
	}

	final void CheckType(Object a_value)
	{
		if(!GetValue().getClass().equals(a_value.getClass()))
		{
			String error = String.format("mismatch types: %s=%s[%s] and %s[%s]"
				, name, GetValue(), GetValue().getClass().getName(), a_value, a_value.getClass().getName());

			throw new ExceptionModelFail(error);
		}
	}

	final void CheckType(Class<?> check_class)
	{
		if(!GetValue().getClass().equals(check_class))
		{
			String error = String.format("mismatch types: %s=%s[%s] and [%s]"
				, name, GetValue(), GetValue().getClass().getName(), check_class.getName());

			throw new ExceptionModelFail(error);
		}
	}

	@Override
	public final String GetString()
	{
		CheckType(String.class);

		return (String) GetValue();
	}

	@Override
	public final Long GetLong()
	{
		CheckType(Long.class);

		return (Long) GetValue();
	}

	@Override
	public final Double GetDouble()
	{
		CheckType(Double.class);

		return (Double) GetValue();
	}

	@Override
	public final Boolean GetBoolean()
	{
		CheckType(Boolean.class);

		return (Boolean) GetValue();
	}

	@Override
	public final Double ToDouble()
	{
		Class<?> my_class = GetValue().getClass();

		if(my_class.equals(Double.class))
			return GetDouble();
		else if(my_class.equals(Long.class))
			return GetLong().doubleValue();
		else
			throw new ExceptionModelFail("bad value type for casting to Double");
	}

	@Override
	public final boolean eq(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckType(new_value);

		return GetValue().equals(new_value);
	}

	@Override
	public final boolean aeq(Object new_value, Object aprx)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckType(new_value);

		Class<?> my_class = GetValue().getClass();

		if(my_class.equals(Double.class))
			return GetDouble() > (Double)new_value - (Double)aprx
				&& GetDouble() < (Double)new_value + (Double)aprx;
		else if(my_class.equals(Long.class))
			return GetLong() > (Long)new_value - (Long)aprx
				&& GetLong() < (Long)new_value + (Long)aprx;
		else
			throw new ExceptionModelFail("bad value type type for approximate comparison");
	}

	@Override
	public final boolean ne(Object new_value)
	{
		return !eq(new_value);
	}

	@Override
	public final boolean gt(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckType(new_value);

		Class<?> my_class = GetValue().getClass();

		if(my_class.equals(Double.class))
			return GetDouble() > (Double)new_value + EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() > (Long)new_value;
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	@Override
	public final boolean lt(Object new_value)
	{
		new_value = SimpleAttribute.ConformType(new_value);
		CheckType(new_value);

		Class<?> my_class = GetValue().getClass();

		if(my_class.equals(Double.class))
			return GetDouble() < (Double)new_value - EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() < (Long)new_value;
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	@Override
	public final boolean ge(Object val)
	{
		return !lt(val);
	}

	@Override
	public final boolean le(Object val)
	{
		return !gt(val);
	}
}
