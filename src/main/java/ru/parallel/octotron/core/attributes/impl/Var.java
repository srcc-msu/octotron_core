/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes.impl;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;

import java.util.Map;

public final class Var extends Attribute
{
	protected final Rule rule;

	public Var(ModelEntity parent, String name, Rule rule)
	{
		super(EAttributeType.VAR, parent, name, Value.undefined);

		this.rule = rule;
	}

	public Rule GetRule()
	{
		return rule;
	}

	@Override
 	public synchronized void UpdateSelf()
	{
		super.UpdateValue(Value.Construct(rule.Compute(GetParent())));
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();

		result.put("ctime", GetCTime());

		return result;
	}
}