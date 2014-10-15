/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.primitive.EEntityType;

public class ModelLink extends ModelEntity
{
	private ModelObject source;
	private ModelObject target;

	public ModelLink(ModelObject source, ModelObject target)
	{
		super(EEntityType.LINK);

		this.target = target;
		this.source = source;
	}

	@Override
	public ModelLinkBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new ModelLinkBuilder(service, this);
	}

	public ModelObject Target()
	{
		return target;
	}

	public ModelObject Source()
	{
		return source;
	}
}
