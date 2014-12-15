/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic.impl;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;

import java.util.Map;

public class Timeout extends ReactionTemplate
{
	public Timeout(String check_name)
	{
		super(check_name);
		InvalidAllowed();
	}

	@Override
	public boolean ReactionNeeded(IModelAttribute attribute)
	{
		if(attribute.GetType() != EAttributeType.SENSOR)
			throw new ExceptionModelFail("Timeout reaction on non-sensor attribute: " + attribute.GetName());

		return ReactionNeeded((SensorAttribute)attribute);
	}

	public boolean ReactionNeeded(SensorAttribute attribute)
	{
		return attribute.IsOutdated();
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = super.GetLongRepresentation();
		result.put("condition", "timeout");
		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = super.GetShortRepresentation();
		result.put("condition", "timeout");
		return result;
	}
}
