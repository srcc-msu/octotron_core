/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.primitive.IPresentable;

import java.util.HashMap;
import java.util.Map;

/**
 * provides a storage for a pair of (name, value)
 * and interface with different comparison functions
 * */
public abstract class BaseAttribute implements IValue, IPresentable
{
	private final String name;
	private Value value;

	BaseAttribute(String name, Value value)
	{
		this.name = name;
		this.value = value;
	}

	public final String GetName()
	{
		return name;
	}

	public Value GetValue()
	{
		return value;
	}

/**
 * set the new value to the attribute
 * tracks matching of the value type, if it is not invalid or undefined
 *
 * TODO: value can change type after getting not computable
 * */
	public void UpdateValue(Value new_value)
	{
		if(value.IsComputable() && new_value.IsComputable())
			value.CheckType(new_value);

		value = new_value;
	}

//--------

	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put(GetName(), value);

		return result;
	}

	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("name", GetName());
		result.put("value", value);

		return result;
	}

	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}

//--------

	@Override
	public String GetString()
	{
		return value.GetString();
	}

	@Override
	public Long GetLong()
	{
		return value.GetLong();
	}

	@Override
	public Double GetDouble()
	{
		return value.GetDouble();
	}

	@Override
	public Boolean GetBoolean()
	{
		return value.GetBoolean();
	}

	@Override
	public Double ToDouble()
	{
		return value.ToDouble();
	}

	@Override
	public boolean eq(Value new_value)
	{
		return value.eq(new_value);
	}

	@Override
	public boolean aeq(Value new_value, Value aprx)
	{
		return value.aeq(new_value, aprx);
	}

	@Override
	public boolean ne(Value new_value)
	{
		return value.ne(new_value);
	}

	@Override
	public boolean gt(Value new_value)
	{
		return value.gt(new_value);
	}

	@Override
	public boolean lt(Value new_value)
	{
		return value.lt(new_value);
	}

	@Override
	public boolean ge(Value new_value)
	{
		return value.ge(new_value);
	}

	@Override
	public boolean le(Value new_value)
	{
		return value.le(new_value);
	}

//--------

	@Override
	public boolean IsDefined()
	{
		return value.IsDefined();
	}

	@Override
	public boolean IsValid()
	{
		return value.IsValid();
	}

	@Override
	public boolean IsComputable()
	{
		return IsDefined() && IsValid();
	}

	@Override
	public String ValueToString()
	{
		return value.ValueToString();
	}

//--------

	public boolean eq(Object new_value)
	{
		return eq(Value.Construct(new_value));
	}

	public boolean aeq(Object new_value, Object aprx)
	{
		return aeq(Value.Construct(new_value), Value.Construct(aprx));
	}

	public boolean ne(Object new_value)
	{
		return ne(Value.Construct(new_value));
	}

	public boolean gt(Object new_value)
	{
		return gt(Value.Construct(new_value));
	}

	public boolean lt(Object new_value)
	{
		return lt(Value.Construct(new_value));
	}

	public boolean ge(Object new_value)
	{
		return ge(Value.Construct(new_value));
	}

	public boolean le(Object new_value)
	{
		return le(Value.Construct(new_value));
	}
//--------

	public boolean eq(BaseAttribute new_value)
	{
		return eq(new_value.GetValue());
	}

	public boolean aeq(BaseAttribute new_value, Object aprx)
	{
		return aeq(new_value.GetValue(), Value.Construct(aprx));
	}

	public boolean ne(BaseAttribute new_value)
	{
		return ne(new_value.GetValue());
	}

	public boolean gt(BaseAttribute new_value)
	{
		return gt(new_value.GetValue());
	}

	public boolean lt(BaseAttribute new_value)
	{
		return lt(new_value.GetValue());
	}

	public boolean ge(BaseAttribute new_value)
	{
		return ge(new_value.GetValue());
	}

	public boolean le(BaseAttribute new_value)
	{
		return le(new_value.GetValue());
	}
}
