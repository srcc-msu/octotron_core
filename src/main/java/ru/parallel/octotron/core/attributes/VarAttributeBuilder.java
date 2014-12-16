/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.exec.services.ModelService;

public class VarAttributeBuilder extends AbstractModAttributeBuilder<VarAttribute>
{
	VarAttributeBuilder(ModelService service, VarAttribute attribute)
	{
		super(service, attribute);
	}

	public final void ConnectDependency()
	{
		for(IModelAttribute dependency
			: attribute.rule.GetDependency(service, attribute.GetParent()))
		{
			dependency.GetBuilder(service).AddDependant(attribute);
			attribute.i_depend_on.add(dependency);
		}
	}
}
