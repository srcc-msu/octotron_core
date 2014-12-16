/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.utils.JavaUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractModAttribute extends AbstractAttribute implements IModelAttribute
{
	private final History history = new History();

	private long ctime = 0;

	protected final Map<Long, Reaction> reactions = new HashMap<>();
	protected final AttributeList<VarAttribute> depend_on_me = new AttributeList<>();

	AbstractModAttribute(EAttributeType type, ModelEntity parent, String name, Value value)
	{
		super(type, parent, name, value);
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

		if(!last.value.IsDefined())
			return 0.0;

		if(last.ctime == 0) // last value was default
			return 0.0;

		if(GetCTime() - last.ctime == 0) // speed is zero
			return 0.0;

		double diff = GetValue().ToDouble() - last.value.ToDouble();

		return diff / (GetCTime() - last.ctime);
	}

	protected void Update(Value new_value)
	{
		history.Add(GetValue(), GetCTime());

		SetValue(new_value);
		SetCTime(JavaUtils.GetTimestamp());

		for(Reaction reaction : GetReactions())
			reaction.Repeat();
	}

	@Override
	public final AttributeList<VarAttribute> GetDependOnMe()
	{
		return depend_on_me;
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
