package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.attribute.DerivedAttribute;
import ru.parallel.octotron.core.model.attribute.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelAttribute extends AttributeDecorator
{
	protected final ModelEntity parent;

	public ModelAttribute(ModelEntity parent, String name)
	{
		super(parent.GetBaseEntity().GetAttribute(name));
		this.parent = parent;
	}

	public ModelEntity GetParent()
	{
		return parent;
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
		return new LinkedList<>();
	}

	public List<OctoResponse> GetFails()
	{
		return new LinkedList<>();
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

	public abstract AttributeList<DerivedAttribute> GetDependant();

	public abstract EAttributeType GetType();
}
