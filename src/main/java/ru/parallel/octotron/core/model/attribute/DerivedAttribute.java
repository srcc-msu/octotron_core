/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.attribute;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.impl.PersistentStorage;

public class DerivedAttribute extends AbstractVaryingAttribute
{
	public DerivedAttribute(ModelEntity parent, String name)
	{
		super(parent, name);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.DERIVED;
	}

	private static final String rule_key_const = "_rule";

	public boolean Update()
	{
		long rule_id = meta.GetAttribute(rule_key_const).GetLong();

		OctoRule rule = PersistentStorage.INSTANCE.GetRules().Get(rule_id);
		Object new_val = rule.Compute(parent);

		return Update(new_val, false);
	}
}
