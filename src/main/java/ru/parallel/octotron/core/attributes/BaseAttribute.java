/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelID;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IPresentable;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseAttribute extends ModelID<EAttributeType> implements IPresentable
{
	private final String name;
	private Value value;

	BaseAttribute(EAttributeType type, String name, Value value)
	{
		super(type);

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

	public final void SetValue(Value new_value)
	{
		if(value.IsComputable() && new_value.IsComputable())
			value.CheckType(new_value);

		value = new_value;
	}

//--------

	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put(GetName(), GetValue());

		return result;
	}

	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("AID", GetID());
		result.put("name", GetName());
		result.put("value", GetValue());

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

	public String GetString()
	{
		return value.GetString();
	}

	public Long GetLong()
	{
		return value.GetLong();
	}

	public Double GetDouble()
	{
		return value.GetDouble();
	}

	public Boolean GetBoolean()
	{
		return value.GetBoolean();
	}

	public Double ToDouble()
	{
		return value.ToDouble();
	}

	public boolean eq(Value new_value)
	{
		return value.eq(new_value);
	}

	public boolean aeq(Value new_value, Value aprx)
	{
		return value.aeq(new_value, aprx);
	}

	public boolean ne(Value new_value)
	{
		return value.ne(new_value);
	}

	public boolean gt(Value new_value)
	{
		return value.gt(new_value);
	}

	public boolean lt(Value new_value)
	{
		return value.lt(new_value);
	}

	public boolean ge(Value new_value)
	{
		return value.ge(new_value);
	}

	public boolean le(Value new_value)
	{
		return value.le(new_value);
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

	public final String GetStringValue()
	{
		return value.ValueToString();
	}
}
