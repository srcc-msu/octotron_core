/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;

public class SensorAttributeBuilder extends AbstractModAttributeBuilder<SensorAttribute>
{
	SensorAttributeBuilder(ModelService service, SensorAttribute attribute)
	{
		super(service, attribute);
	}

	public void SetValid(Boolean is_valid)
	{
		attribute.SetIsUserValid(is_valid);
	}

	public void SetTimeoutReaction(ReactionTemplate response)
	{
		if(attribute.timeout_reaction != null)
			throw new ExceptionModelFail("timeout reaction for attribute " + attribute.GetName() + " is already set");

		attribute.timeout_reaction = new Reaction(response, attribute);
	}
}
