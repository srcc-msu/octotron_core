/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.exec.services.ModelService;

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
}
