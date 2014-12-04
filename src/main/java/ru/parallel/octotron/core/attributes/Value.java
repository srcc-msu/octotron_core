package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.utils.JavaUtils;

import java.text.DecimalFormat;

public class Value
{
	private final Object value;
	private final Class<?> my_class;

	private Value(Object value, Class<?> my_class)
	{
		this.value = value;
		this.my_class = my_class;
	}

	/**
	 * tries to convert unchecked Object to the checked Value
	 * if it is a value already - makes a copy
	 * */
	public static Value Construct(Object value)
	{
		if(value == null)
			throw new ExceptionModelFail("Value can not be null");

		if(value instanceof Value)
			return new Value(((Value) value).value, ((Value) value).my_class); // so cute...
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

	private static final DecimalFormat df = new DecimalFormat("0.00"); // TODO: is it ok?

	public final String ValueToString()
	{
		if(my_class.equals(Boolean.class))
			return value.toString();
		else if(my_class.equals(Long.class))
			return value.toString();
		else if(my_class.equals(Double.class))
		{
			return df.format(value);
		}
		else if(my_class.equals(String.class))
			return JavaUtils.Quotify((String)value);

		else
			throw new ExceptionModelFail("unexpected value: " + value + " : " + my_class);
	}

	public static Value ValueFromStr(String value_str)
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

// ------------------------

	public final String GetString()
	{
		CheckType(String.class);

		return (String) value;
	}

	public final Long GetLong()
	{
		CheckType(Long.class);

		return (Long) value;
	}

	public final Double GetDouble()
	{
		CheckType(Double.class);

		return (Double) value;
	}

	public final Boolean GetBoolean()
	{
		CheckType(Boolean.class);

		return (Boolean) value;
	}

	public final Double ToDouble()
	{
		if(my_class.equals(Double.class))
			return GetDouble();
		else if(my_class.equals(Long.class))
			return GetLong().doubleValue();
		else
			throw new ExceptionModelFail("bad value type for casting to Double: ");
	}

// ---------------------

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

// -----------------------------

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof Value))
			return false;

		Value cmp = ((Value)object);

		return value.equals(cmp.value);
	}

	public final boolean eq(Value new_value)
	{
		CheckType(new_value);

		return equals(new_value);
	}

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
			throw new ExceptionModelFail("bad value type type for approximate comparison");
	}

	public final boolean ne(Value new_value)
	{
		return !eq(new_value);
	}

	public static final double EPSILON = 0.00001;

	public final boolean gt(Value new_value)
	{
		CheckType(new_value);

		if(my_class.equals(Double.class))
			return GetDouble() > new_value.GetDouble() + EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() > new_value.GetLong();
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	public final boolean lt(Value new_value)
	{
		CheckType(new_value);

		if(my_class.equals(Double.class))
			return GetDouble() < new_value.GetDouble() - EPSILON;
		else if(my_class.equals(Long.class))
			return GetLong() < new_value.GetLong();
		else
			throw new ExceptionModelFail("bad value type type for comparison");
	}

	public final boolean ge(Value val)
	{
		return !lt(val);
	}

	public final boolean le(Value val)
	{
		return !gt(val);
	}

	public Object GetRaw()
	{
		return value;
	}
}
