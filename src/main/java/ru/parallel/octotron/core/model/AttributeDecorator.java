package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
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
	public IEntity GetParent()
	{
		return null;
	}

	@Override
	public String GetName()
	{
		return null;
	}

	@Override
	public Object GetValue()
	{
		return null;
	}

	@Override
	public void SetValue(Object value)
	{

	}

	@Override
	public String GetString()
	{
		return null;
	}

	@Override
	public Long GetLong()
	{
		return null;
	}

	@Override
	public Double GetDouble()
	{
		return null;
	}

	@Override
	public Boolean GetBoolean()
	{
		return null;
	}

	@Override
	public Double ToDouble()
	{
		return null;
	}

	@Override
	public boolean eq(Object new_value)
	{
		return false;
	}

	@Override
	public boolean aeq(Object new_value, Object aprx)
	{
		return false;
	}

	@Override
	public boolean ne(Object new_value)
	{
		return false;
	}

	@Override
	public boolean gt(Object new_value)
	{
		return false;
	}

	@Override
	public boolean lt(Object new_value)
	{
		return false;
	}

	@Override
	public boolean ge(Object val)
	{
		return false;
	}

	@Override
	public boolean le(Object val)
	{
		return false;
	}
}
