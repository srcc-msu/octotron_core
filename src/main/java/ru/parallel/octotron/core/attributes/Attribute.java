/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelInfo;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

import java.util.Map;

/**
 * base class for all attributes on entities
 * supports dependencies between attributes
 * */
public abstract class Attribute extends BaseAttribute
{
	private ModelInfo<EAttributeType> info;
	private final ModelEntity parent;

/**
 * last modification time
 * */
	private long ctime = 0;

/**
 * history, that stores last values and modification times
 * */
	private final AttributeHistory history = new AttributeHistory();

	protected final AttributeList<Attribute> depend_on_me = new AttributeList<>();
	protected final AttributeList<Attribute> i_depend_on = new AttributeList<>();

	public Attribute(EAttributeType type, ModelEntity parent, String name, Value value)
	{
		super(name, value);

		this.info = new ModelInfo<>(type);

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

/**
 * compute speed of the attribute, using current value and history
 * may return invalid value if it is not possible
 * */
	public final Value GetSpeed()
	{
		if(!IsComputable())
			return Value.invalid;

		AttributeHistory.Entry last = history.GetLast();

		if(!last.value.IsComputable())
			return Value.invalid;

		if(last.ctime == 0) // last value was default
			return Value.invalid;

		if(GetCTime() - last.ctime == 0) // speed is zero
			return new Value(0.0);

		double diff = ToDouble() - last.value.ToDouble();

		return new Value(diff / (GetCTime() - last.ctime));
	}

/**
 * set the new value to the attribute, add history entry
 * update self and all dependant attributes
 * */
	@Override
	public void UpdateValue(Value new_value)
	{
		history.Add(GetValue(), GetCTime());

		super.UpdateValue(new_value);
		SetCTime(JavaUtils.GetTimestamp());

		UpdateDependant();
	}

	protected abstract void UpdateSelf();

/**
 * update all dependant attributes, which are computable now
 * */
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

/**
 * check that all needed attributes are defined
 * attributes may be undefined only in the beginning
 * when the check passes once - no more checks will be performed
 * */
	boolean DependenciesDefined()
	{
		if(dependencies_defined)
			return true;

		for(Attribute attribute : i_depend_on)
		{
			if(!attribute.IsDefined())
				return false;
		}

		// we've got positive for all, cache it for future - it will not change back
		dependencies_defined = true;
		return true;
	}

/*	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();
		result.put("parent_aid", GetParent().GetInfo().GetID());

		return result;
	}

	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();
		result.put("parent_aid", GetParent().GetInfo().GetID());
		result.put("attribute_aid", GetInfo().GetID());

		return result;
	}*/

	public ModelInfo<EAttributeType> GetInfo()
	{
		return info;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof Attribute))
			return false;

		Attribute cmp = ((Attribute)object);

		return info.equals(cmp.info);
	}
}
