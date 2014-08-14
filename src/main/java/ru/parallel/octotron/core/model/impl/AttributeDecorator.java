package ru.parallel.octotron.core.model.impl;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;

public class AttributeDecorator implements IAttribute
{
	private final GraphAttribute base;

	public AttributeDecorator(GraphAttribute base)
	{
		this.base = base;
	}

	protected GraphAttribute GetBase()
	{
		return base;
	}

// decorated staff

	@Override
	public String GetName()
	{
		return base.GetName();
	}

	@Override
	public Object GetValue()
	{
		return base.GetValue();
	}

	@Override
	public void SetValue(Object value)
	{
		base.SetValue(value);
	}

	@Override
	public String GetString()
	{
		return base.GetString();
	}

	@Override
	public Long GetLong()
	{
		return base.GetLong();
	}

	@Override
	public Double GetDouble()
	{
		return base.GetDouble();
	}

	@Override
	public Boolean GetBoolean()
	{
		return base.GetBoolean();
	}

	@Override
	public Double ToDouble()
	{
		return base.ToDouble();
	}

	@Override
	public boolean eq(Object new_value)
	{
		return base.eq(new_value);
	}

	@Override
	public boolean aeq(Object new_value, Object aprx)
	{
		return base.aeq(new_value, aprx);
	}

	@Override
	public boolean ne(Object new_value)
	{
		return base.ne(new_value);
	}

	@Override
	public boolean gt(Object new_value)
	{
		return base.gt(new_value);
	}

	@Override
	public boolean lt(Object new_value)
	{
		return base.lt(new_value);
	}

	@Override
	public boolean ge(Object new_value)
	{
		return base.ge(new_value);
	}

	@Override
	public boolean le(Object new_value)
	{
		return base.le(new_value);
	}
}
