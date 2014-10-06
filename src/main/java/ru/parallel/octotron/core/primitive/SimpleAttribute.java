/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.utils.JavaUtils;

import java.text.DecimalFormat;

/**
 * base class for attributes, contains some common operations<br>
 * */
public class SimpleAttribute
{
	protected final String name;
	protected Object value;

	public SimpleAttribute(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public final String GetName()
	{
		return name;
	}

	public final Object GetValue()
	{
		return value;
	}

// --------------------------------
//			CONVERTERS
//---------------------------------

	public static Object ValueFromStr(String value_str)
		throws ExceptionParseError
	{
		Object value = null;

		int str_len = value_str.length();

		if(str_len == 0)
			throw new ExceptionParseError("can not get value from empty string");

		char last_char = value_str.charAt(str_len - 1);
		char first_char = value_str.charAt(0);

		if(first_char == '"' && last_char == '"')
		{
			return value_str.substring(1, value_str.length() - 1);
		}

		try { value = Long.parseLong(value_str); }
			catch(NumberFormatException ignore){}

		if(value == null)
			try { value = Double.parseDouble(value_str); }
				catch(NumberFormatException ignore){}

		if(value == null) if(value_str.equals("true")) value = true;
		if(value == null) if(value_str.equals("false")) value = false;

		if(value == null) if(value_str.equals("True")) value = true;
		if(value == null) if(value_str.equals("False")) value = false;

		if(value == null) value = value_str;

		return value;
	}

	public static Object ConformType(Object value)
	{
		if(value instanceof Integer)
			return ((Integer) value).longValue();
		else if(value instanceof Float)
			return ((Float) value).doubleValue();
		else
			return value;
	}

	public static String ValueToStr(Object value)
	{
		value = SimpleAttribute.ConformType(value);

		if(value instanceof Boolean)
			return value.toString();
		else if(value instanceof Long)
			return value.toString();
		else if(value instanceof Double)
		{
			DecimalFormat df = new DecimalFormat("0.00"); // TODO: is it ok?
			return df.format(value);
		}
		else if(value instanceof String)
			return JavaUtils.Quotify((String)value);
		else
			throw new ExceptionModelFail("unsupported type: " + value.getClass());
	}

	public String GetStringValue()
	{
		return ValueToStr(GetValue());
	}
}
