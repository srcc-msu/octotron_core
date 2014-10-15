/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.utils.JavaUtils;

import java.util.*;

public abstract class AbstractModAttribute extends AbstractAttribute implements IModelAttribute
{
	private final History history = new History();

	private boolean is_valid = true;
	private long ctime = 0;

	protected final Map<Long, Reaction> reactions = new HashMap<>();
	protected final AttributeList<VarAttribute> dependant = new AttributeList<>();

	AbstractModAttribute(EAttributeType type, ModelEntity parent, String name, Object value)
	{
		super(type, parent, name, value);
	}

	public abstract AbstractModAttributeBuilder<? extends AbstractModAttribute> GetBuilder(ModelService service);

	@Override
	public boolean CheckValid()
	{
		return is_valid && GetCTime() != 0L;
	}

	@Override
	public void SetValid()
	{
		is_valid = true;
	}

	@Override
	public void SetInvalid()
	{
		is_valid = false;
	}

// ---------------

	public void SetValid(boolean is_valid)
	{
		this.is_valid = is_valid;
	}

	public boolean GetIsValid()
	{
		return is_valid;
	}

	public long GetCTime()
	{
		return ctime;
	}

	public void SetCTime(long new_ctime)
	{
		ctime = new_ctime;
	}

	public Reaction GetReaction(long id)
	{
		return reactions.get(id);
	}

	@Override
	public double GetSpeed()
	{
		History.Entry last = history.GetLast();

		if(last == null)
			return 0.0;

		long last_ctime = last.ctime;

		if(GetCTime() - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - (Double)last.value;

		return diff / (GetCTime() - last_ctime);
	}

	protected boolean Update(Object new_value)
	{
		for(Reaction reaction : GetReactions())
			reaction.Repeat(new_value);

		boolean result = (GetValue() != new_value);
		history.Add(GetValue(), GetCTime());

		SetCTime(JavaUtils.GetTimestamp());
		SetValue(new_value);

		return result;
	}

	@Override
	public AttributeList<VarAttribute> GetDependant()
	{
		return dependant;
	}

	@Override
	public Collection<Reaction> GetReactions()
	{
		return reactions.values();
	}
}
