/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;

public abstract class LinkRule extends Rule
{
	protected LinkRule() {}

	@Override
	public final Object Compute(ModelEntity entity, Attribute rule_attribute)
	{
		return Compute((ModelLink) entity);
	}

	public abstract Object Compute(ModelLink object);

	@Override
	public final AttributeList<Attribute> GetDependency(ModelEntity entity)
	{
		return GetDependency((ModelLink) entity);
	}

	public abstract AttributeList<Attribute> GetDependency(ModelLink object);
}
