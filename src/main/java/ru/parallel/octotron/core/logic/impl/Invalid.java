/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;

import java.util.Map;

public class Invalid extends ReactionTemplate
{
	public Invalid(String check_name)
	{
		super(check_name);
		InvalidAllowed();
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		return !attribute.GetValue().IsValid();
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();
		result.put("condition", "invalid");

		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();
		result.put("condition", "invalid");

		return result;
	}
}
