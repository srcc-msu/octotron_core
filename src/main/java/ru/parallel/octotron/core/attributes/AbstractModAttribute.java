/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractModAttribute extends AbstractAttribute implements IModelAttribute
{
	private final History history = new History();

	private long ctime = 0;

	protected final Map<Long, Reaction> reactions = new HashMap<>();
	protected final AttributeList<VarAttribute> dependant = new AttributeList<>();

	/**
	 * tracks if the sensor got at least one value update:
	 * initially or via Update()
	 * */
	private boolean has_value = false;

	AbstractModAttribute(EAttributeType type, ModelEntity parent, String name, Object value)
	{
		super(type, parent, name, value);
		has_value = true;
	}

	AbstractModAttribute(EAttributeType type, ModelEntity parent, String name)
	{
		super(type, parent, name, null);
		has_value = false;
	}

	public final boolean HasValue()
	{
		return has_value;
	}

	public abstract AbstractModAttributeBuilder<? extends AbstractModAttribute> GetBuilder(ModelService service);

// ------------------------------------------

	@Override
	public final long GetCTime()
	{
		return ctime;
	}

	final void SetCTime(long new_ctime)
	{
		ctime = new_ctime;
	}

// ------------------------------------------

	@Override
	public final double GetSpeed()
	{
		History.Entry last = history.GetLast();

		if(last == null)
			return 0.0;

		long last_ctime = last.ctime;

		if(GetCTime() - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - ToDouble(last.value);

		return diff / (GetCTime() - last_ctime);
	}

	protected void Update(Object new_value)
	{
		for(Reaction reaction : GetReactions())
			reaction.Repeat(new_value);

		history.Add(GetValue(), GetCTime());

		SetCTime(JavaUtils.GetTimestamp());
		SetValue(new_value);
	}

	@Override
	public final AttributeList<VarAttribute> GetDependant()
	{
		return dependant;
	}


	@Override
	public final Collection<Reaction> GetReactions()
	{
		return reactions.values();
	}

	public final Reaction GetReaction(long id)
	{
		return reactions.get(id);
	}

// ------------------------------------------------------------------------------------------------------

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put(GetName(), GetValue());

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("AID", GetID());
		result.put("parent", GetParent().GetID());
		result.put("name", GetName());
		result.put("value", GetValue());
		result.put("ctime", GetCTime());
		result.put("has_value", HasValue());

		return result;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}
}
