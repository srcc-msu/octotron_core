/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.VaryingObject;

public class VaryingAttribute extends AbstractVaryingAttribute<VaryingObject>
{
	public VaryingAttribute(ModelEntity parent, GraphAttribute attribute, VaryingObject meta)
	{
		super(parent, attribute, meta);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.VARYING;
	}

	public Rule GetRule()
	{
		return meta.GetRule();
	}

	public boolean Update()
	{
		Rule rule = meta.GetRule();
		Object new_val = rule.Compute(parent);

		return InnerUpdate(new_val);
	}
}
