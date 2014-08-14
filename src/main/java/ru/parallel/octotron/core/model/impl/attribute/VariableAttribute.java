/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.VariableObject;
import ru.parallel.octotron.core.rule.OctoRule;

public class VariableAttribute extends AbstractVaryingAttribute<VariableObject>
{
	public VariableAttribute(ModelEntity parent, VariableObject meta, String name)
	{
		super(parent, meta, name);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.VARIABLE;
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
