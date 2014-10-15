/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.IAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public abstract class AbstractAttribute extends UniqueID<EAttributeType> implements IAttribute
{
	public static final double EPSILON = 0.00001;

	private final ModelEntity parent;

	private final String name;
	private Object value;

	AbstractAttribute(EAttributeType type, ModelEntity parent, String name, Object value)
	{
		super(type);

		this.parent = parent;

		this.name = name;
		this.value = value;
	}

	@Override
	public final String GetName()
	{
		return name;
	}

	@Override
	public final Object GetValue()
	{
		return value;
	}

	final void SetValue(Object new_value)
	{
		value = new_value;
	}

	private void CheckType(Object a_value)
	{
		if(!GetValue().getClass().equals(a_value.getClass()))
		{
			String error = String.format("mismatch types: %s=%s[%s] and %s[%s]"
				, GetName(), GetValue(), GetValue().getClass().getName(), a_value, a_value.getClass().getName());

			throw new ExceptionModelFail(error);
		}
	}

	private void CheckType(Class<?> check_class)
	{
		if(!GetValue().getClass().equals(check_class))
		{
			String error = String.format("mismatch types: %s=%s[%s] and [%s]"
				, GetName(), GetValue(), GetValue().getClass().getName(), check_class.getName());

			throw new ExceptionModelFail(error);
		}
	}

	@Override
	public final ModelEntity GetParent()
	{
		return parent;
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

	@Override
	public final String GetStringValue()
	{
		return SimpleAttribute.ValueToStr(GetValue());
	}
}
