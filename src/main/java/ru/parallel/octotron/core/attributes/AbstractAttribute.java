/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelID;
import ru.parallel.octotron.core.primitive.EAttributeType;

public abstract class AbstractAttribute extends ModelID<EAttributeType> implements IAttribute
{
	private final ModelEntity parent;
	private final String name;

	private Value value;

	AbstractAttribute(EAttributeType type, ModelEntity parent, String name, Value value)
	{
		super(type);

		this.parent = parent;

		this.name = name;
		this.value = value;
	}

	@Override
	public final ModelEntity GetParent()
	{
		return parent;
	}

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

// ---------

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


// ---------


	@Override
	public final String GetName()
	{
		return name;
	}

	@Override
	public final Value GetValue()
	{
		return value;
	}

	final void SetValue(Value new_value)
	{
		value = new_value;
	}

	@Override
	public final String GetStringValue()
	{
		return value.ValueToString();
	}
}
