/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.meta.DerivedObject;
import ru.parallel.octotron.core.model.meta.DerivedObjectFactory;
import ru.parallel.octotron.core.rule.OctoRule;

public class DerivedAttribute extends AbstractVaryingAttribute<DerivedObject>
{
	public DerivedAttribute(ModelEntity parent, DerivedObject meta, String name)
	{
		super(parent, meta, name);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.DERIVED;
	}

	public boolean Update()
	{
		OctoRule rule = ((DerivedObject)meta).GetRule();
		Object new_val = rule.Compute(parent);

		return Update(new_val, false);
	}
}
