/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
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
		attribute.SetIsValid(is_valid);
	}

	public void SetTimeoutReaction(ReactionTemplate timeout_reaction)
	{
		attribute.timeout_reaction = new Reaction(timeout_reaction, attribute);
	}
}
