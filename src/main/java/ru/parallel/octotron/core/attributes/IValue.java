/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.impl.Value;

/**
 * interface for dynamically typed value
 * acceptable types are: string, long, double, boolean
 * supports special values types: undefined and invalid
 * */
public interface IValue
{
	boolean IsDefined();
	boolean IsValid();
	boolean IsComputable();

	String GetString();
	Long GetLong();
	Double GetDouble();
	Boolean GetBoolean();

/**
 * return string representation of the value
 * */
	String ValueToString();

/**
 * convert the value to double, if possible
 * */
	Double ToDouble();

	boolean eq(Value new_value);
	boolean aeq(Value new_value, Value aprx);
	boolean ne(Value new_value);
	boolean gt(Value new_value);
	boolean lt(Value new_value);
	boolean ge(Value val);
	boolean le(Value val);
}
