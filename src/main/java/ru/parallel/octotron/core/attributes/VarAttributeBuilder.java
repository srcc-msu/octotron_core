/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelService;

public class VarAttributeBuilder extends AbstractModAttributeBuilder<VarAttribute>
{
	VarAttributeBuilder(ModelService service, VarAttribute attribute)
	{
		super(service, attribute);
	}

	public void ConnectDependency()
	{
		for(IModelAttribute dependency
			: attribute.rule.GetDependency(attribute.GetParent()))
		{
			dependency.GetBuilder(service).AddDependant(attribute);
			attribute.i_depend_from.add(dependency);
		}
	}
}
