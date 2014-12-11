/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;

import java.util.Map;

public class NotEquals extends ReactionTemplate
{
	private final Value check_value;

	public NotEquals(String check_name, Object check_value)
	{
		super(check_name);

		this.check_value = Value.Construct(check_value);
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		return attribute.ne(GetCheckValue());
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();
		result.put("condition", "not_equals");
		result.put("check_value", GetCheckValue());

		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();
		result.put("condition", "not_equals");
		result.put("check_value", GetCheckValue());

		return result;
	}

	public final Value GetCheckValue()
	{
		return check_value;
	}
}
