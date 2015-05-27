/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

public abstract class Attribute extends BaseAttribute
{
	private final ModelEntity parent;

	private final AttributeHistory history = new AttributeHistory();
	private long ctime = 0;

	protected final AttributeList<Attribute> depend_on_me = new AttributeList<>();
	protected final AttributeList<Attribute> i_depend_on = new AttributeList<>();

	public Attribute(EAttributeType type, ModelEntity parent, String name, Value value)
	{
		super(type, name, value);

		this.parent = parent;
	}

//--------

	public final ModelEntity GetParent()
	{
		return parent;
	}

	public final long GetCTime()
	{
		return ctime;
	}

	public final void SetCTime(long new_ctime)
	{
		ctime = new_ctime;
	}

//--------

	public void AddDependOnMe(Attribute dependant)
	{
		depend_on_me.add(dependant);
	}

	public void AddIDependOn(Attribute dependant)
	{
		i_depend_on.add(dependant);
	}

	public final AttributeList<Attribute> GetDependOnMe()
	{
		return depend_on_me;
	}

	public final AttributeList<Attribute> GetIDependOn()
	{
		return i_depend_on;
	}

//--------

	public final Value GetSpeed()
	{
		if(!IsComputable())
			return Value.invalid;

		AttributeHistory.Entry last = history.GetLast();

		if(!last.value.IsComputable())
			return Value.invalid;

		if(last.ctime == 0) // last value was default
			return new Value(0.0);

		if(GetCTime() - last.ctime == 0) // speed is zero
			return new Value(0.0);

		double diff = ToDouble() - last.value.ToDouble();

		return new Value(diff / (GetCTime() - last.ctime));
	}

	@Override
	public void UpdateValue(Value new_value)
	{
		history.Add(GetValue(), GetCTime());

		super.UpdateValue(new_value);
		SetCTime(JavaUtils.GetTimestamp());

		UpdateDependant();
	}

	protected abstract void UpdateSelf();

	public final void UpdateDependant()
	{
		for(Attribute attribute : depend_on_me)
		{
			if(!attribute.DependenciesDefined())
				continue;

			attribute.UpdateSelf();
			attribute.UpdateDependant();
		}
	}

	private boolean dependencies_defined = false;

	private boolean DependenciesDefined()
	{
		if(dependencies_defined)
			return true;

		for(Attribute attribute : i_depend_on)
		{
			if(!attribute.IsDefined())
			{
				dependencies_defined = false;
				return false;
			}
		}

		// we've got positive for all, cache it for future - it will not change back
		dependencies_defined = true;
		return true;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("ctime", ctime);

		return result;
	}
}
