/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.model.IModelAttribute;

import java.util.Map;

public class NotEquals extends ReactionTemplate
{
	public NotEquals(String check_name, Object check_value)
	{
		super(check_name, check_value);
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		if(!attribute.Check())
			return false;

		return attribute.ne(GetCheckValue());
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();
		result.put("comparison", "not_equals");
		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();
		result.put("comparison", "not_equals");
		return result;
	}
}
