/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.attributes.EAttributeType;
import ru.parallel.octotron.bg_services.ServiceLocator;

import java.util.Map;

/**
 * var is an attributes, that calculates its value
 * using a special Rule
 * */
public final class Var extends Attribute
{
	protected final Rule rule;

	public Var(ModelEntity parent, String name, Rule rule)
	{
		super(EAttributeType.VAR, parent, name, Value.undefined, 0);

		this.rule = rule;
	}

	public Rule GetRule()
	{
		return rule;
	}

	@Override
	public void UpdateSelf(long current_time)
	{
		Object raw_value = rule.Compute(GetParent(), this);
		super.Update(Value.Construct(raw_value), current_time);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterVar(this);
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("ctime", GetCTime());

		return result;
	}
}
