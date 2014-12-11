/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.exec.services.ModelService;

public class ModelLinkBuilder extends ModelEntityBuilder<ModelLink>
{
	ModelLinkBuilder(ModelService service, ModelLink entity)
	{
		super(service, entity);
	}
}
