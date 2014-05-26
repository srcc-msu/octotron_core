/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoObjectRule;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.SimpleAttribute;

public abstract class Mirror extends OctoObjectRule
{
	private static final long serialVersionUID = -2328141171759693459L;
	private String mirror_attribute;
	private String mirror_name_match;
	private Object mirror_value_match;

	protected Mirror(String mirror_attribute, SimpleAttribute mirror_parent)
	{
		super(mirror_attribute);
		this.mirror_attribute = mirror_attribute;
		this.mirror_name_match = mirror_parent.GetName();
		this.mirror_value_match = mirror_parent.GetValue();
	}

	@Override
	public final Object Compute(OctoObject object)
	{
		return object.GetInNeighbors().append(object.GetOutNeighbors())
			.Filter(mirror_name_match, mirror_value_match)
			.Only().GetAttribute(mirror_attribute).GetValue();
	}

	@Override
	public final EDependencyType GetDeps()
	{
		return EDependencyType.ALL;
	}
}
