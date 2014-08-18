/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.VaryingObject;
import ru.parallel.octotron.core.OctoRule;

public class VaryingAttribute extends AbstractVaryingAttribute<VaryingObject>
{
	public VaryingAttribute(ModelEntity parent, VaryingObject meta, String name)
	{
		super(parent, meta, name);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.VARYING;
	}

	public OctoRule GetRule()
	{
		return meta.GetRule();
	}

	public boolean Update()
	{
		OctoRule rule = meta.GetRule();
		Object new_val = rule.Compute(parent);

		return Update(new_val, false);
	}
}
