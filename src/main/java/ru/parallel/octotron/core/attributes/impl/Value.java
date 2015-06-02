package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.IValue;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.utils.JavaUtils;

import java.text.DecimalFormat;

public class Value implements IValue
{
	private static class Undefined
	{
		private Undefined() {}

		public static final Undefined value = new Undefined();

		@Override
		public final String toString()
		{
			return "undefined";
		}
	}

	private static class Invalid
	{
		private Invalid() {}

		public static final Invalid value = new Invalid();

		@Override
		public final String toString()
		{
			return "invalid";
		}
	}

//--------

	private final Object value;
	private final Class<?> my_class;

	private Value(Object value, Class<?> my_class)
	{
		this.value = value;
		this.my_class = my_class;
	}

	public Value(Value value)
	{
		this.value = value.value;
		this.my_class = value.my_class;
	}

	public static final Value undefined = new Value(Undefined.value, Undefined.class);
	public static final Value invalid = new Value(Invalid.value, Invalid.class);

	@Override
	public boolean IsDefined()
	{
		return !equals(undefined);
	}

	@Override
	public boolean IsValid()
	{
		return !equals(invalid);
	}

	@Override
	public boolean IsComputable() { return IsDefined() && IsValid(); }

	/**
	 * tries to convert unchecked Object to the checked Value
	 * if it is a value already - does nothing
	 * */
	public static Value Construct(Object value)
	{
		if(value == null)
			throw new ExceptionModelFail("Value can not be null");

		if(value instanceof Value)
			return (Value) value;
		else if(value instanceof Long)
			return new Value(value, Long.class);
		else if(value instanceof Integer)
			return new Value(((Integer)value).longValue(), Long.class);
		else if(value instanceof Double)
			return new Value(value, Double.class);
		else if(value instanceof Float)
			return new Value(((Float)value).doubleValue(), Double.class);
		else if(value instanceof Boolean)
			return new Value(value, Boolean.class);
		else if(value instanceof String)
			return new Value(value, String.class);

		else
			throw new ExceptionModelFail("unsupported value type: " + value + " : " + value.getClass().getName());
	}

	public Value(Long value)
	{
		this(value, value.getClass());
	}

	public Value(Double value)
	{
		this(value, value.getClass());
	}

	public Value(Boolean value)
	{
		this(value, value.getClass());
	}

	public Value(String value)
	{
		this(value, value.getClass());
	}

	public Value(Integer value)
	{
		this(value.longValue(), Long.class);
	}

	public Value(Float value)
	{
		this(value.doubleValue(), Double.class);
	}

	private static final DecimalFormat decimal_format = new DecimalFormat("0.00"); // TODO: is it ok?

	@Override
	public final String ValueToString()
	{
		if(my_class.equals(Long.class))
			return value.toString();

		if(my_class.equals(Double.class))
			return decimal_format.format(value);

		if(my_class.equals(Boolean.class))
			return value.toString();

		if(my_class.equals(String.class))
			return JavaUtils.Quotify((String)value);

		if(my_class.equals(Undefined.class))
			return JavaUtils.Quotify(value.toString());

		if(my_class.equals(Invalid.class))
			return JavaUtils.Quotify(value.toString());

		else
			throw new ExceptionModelFail("unexpected value: " + value + " : " + my_class);
	}

	public static Value ValueFromString(String value_str)
		throws ExceptionParseError
	{
		int str_len = value_str.length();

		if(str_len == 0)
			throw new ExceptionParseError("can not get value from empty string");

		char last_char = value_str.charAt(str_len - 1);
		char first_char = value_str.charAt(0);

		if(first_char == '"' && last_char == '"')
		{
			return new Value(value_str.substring(1, value_str.length() - 1));
		}

		try { return new Value(Long.parseLong(value_str)); }
			catch(NumberFormatException ignore){}

		try { return new Value(Double.parseDouble(value_str)); }
			catch(NumberFormatException ignore){}

		if(value_str.equals("true")) return new Value(true);
		if(value_str.equals("false")) return new Value(false);

		if(value_str.equals("True")) return new Value(true);
		if(value_str.equals("False")) return new Value(false);

		return new Value(value_str);
	}

	public final Class<?> GetClass()
	{
		return my_class;
	}

//--------

	@Override
	public final String GetString()
	{
		CheckType(String.class);

		return (String) value;
	}

	@Override
	public final Long GetLong()
	{
		CheckType(Long.class);

		return (Long) value;
	}

	@Override
	public final Double GetDouble()
	{
		CheckType(Double.class);

		return (Double) value;
	}

	@Override
	public final Boolean GetBoolean()
	{
		CheckType(Boolean.class);

		return (Boolean) value;
	}

	@Override
	public final Double ToDouble()
	{
		if(my_class.equals(Double.class))
			return GetDouble();
		else if(my_class.equals(Long.class))
			return GetLong().doubleValue();
		else
		{
			String error = String.format("bad value type for casting to Double: %s[%s]"
				, ValueToString(), my_class.toString());

			throw new ExceptionModelFail(error);
		}
	}

//--------

	public void CheckType(Value check_value)
	{
		if(!my_class.equals(check_value.my_class))
		{
			String error = String.format("mismatch types: %s[%s] and %s[%s]"
				, value, my_class.getName()
				, check_value.value, check_value.my_class.getName());

			throw new ExceptionModelFail(error);
		}
	}

	public void CheckType(Class<?> check_class)
	{
		if(!my_class.equals(check_class))
		{
			String error = String.format("mismatch types: %s[%s] and [%s]"
				, value, my_class.getName()
				, check_class);

			throw new ExceptionModelFail(error);
		}
	}

//--------

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof Value))
			return false;

		Value cmp = ((Value)object);

		return value.equals(cmp.value);
	}

	@Override
	public final boolean eq(Value new_value)
	{
		CheckType(new_value);

		return equals(new_value);
	}

	@Override
	public final boolean aeq(Value new_value, Value aprx)
	{
		CheckType(new_value);

		if(my_class.equals(Double.class))
			return GetDouble() > new_value.GetDouble() - aprx.GetDouble()
				&& GetDouble() < new_value.GetDouble() + aprx.GetDouble();
		else if(my_class.equals(Long.class))
			return GetLong() > new_value.GetLong() - aprx.GetLong()
				&& GetLong() < new_value.GetLong() + aprx.GetLong();
		else
		{
			String error = String.format("bad value type type for approximate comparison: %s[%s]"
				, ValueToString(), my_class.toString());
			throw new ExceptionModelFail(error);
		}
	}

	@Override
	public final boolean ne(Value new_value)
	{
		return !eq(new_value);
	}

	public static final double EPSILON = 0.00001;

	@Override
	public final boolean gt(Value new_value)
	{
		CheckType(new_value);

		if(my_class.equals(Double.class))
			return GetDouble() > new_value.GetDouble() + EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() > new_value.GetLong();
		else
		{
			String error = String.format("bad value type type for comparison: %s[%s]"
				, ValueToString(), my_class.toString());
			throw new ExceptionModelFail(error);
		}
	}

	@Override
	public final boolean lt(Value new_value)
	{
		CheckType(new_value);

		if(my_class.equals(Double.class))
			return GetDouble() < new_value.GetDouble() - EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() < new_value.GetLong();
		else
		{
			String error = String.format("bad value type type for comparison: %s[%s]"
				, ValueToString(), my_class.toString());
			throw new ExceptionModelFail(error);
		}
	}

	@Override
	public final boolean ge(Value val)
	{
		return !lt(val);
	}

	@Override
	public final boolean le(Value val)
	{
		return !gt(val);
	}

	public Object GetRaw()
	{
		return value;
	}

	@Override
	public String toString()
	{
		String res = super.toString();

		return  res + " : " + ValueToString();
	}
}
