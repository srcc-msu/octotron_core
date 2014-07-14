package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.attribute.Derived;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelAttribute extends AttributeDecorator
{
	public static final String extended_attribute = "_extended_attribute";

	protected ModelEntity parent;

	public ModelAttribute(ModelEntity parent, String name)
	{
		super(parent.GetBaseEntity().GetAttribute(name));
		this.parent = parent;
	}

	@Override
	public ModelEntity GetParent()
	{
		return ModelEntity.Obtain(GetBase().GetParent());
	}

	public Object GetLastValue()
	{
		return GetValue();
	}

	protected boolean Update(Object new_value, boolean allow_overwrite)
	{
		if(ne(new_value) || GetCTime() == 0 || allow_overwrite)
		{
			SetValue(new_value);
			return true;
		}

		return false;
	}

	public long GetCTime()
	{
		return 0L;
	}

	public long GetATime()
	{
		return 0L;
	}

	public double GetSpeed()
	{
		return 0.0;
	}

	public List<OctoResponse> PreparePendingReactions()
	{
		return new LinkedList<OctoResponse>();
	}

	public List<OctoResponse> GetFails()
	{
		return new LinkedList<OctoResponse>();
	}

	public boolean IsValid()
	{
		return true;
	}

	public void SetValid()
	{
		throw new ExceptionModelFail("updating raw attribute");
	}

	public void SetInvalid()
	{
		throw new ExceptionModelFail("updating raw attribute");
	}

	public abstract AttributeList<Derived> GetDependant();
}
