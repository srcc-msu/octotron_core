/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.core.OctoObjectRule;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelObject;

public abstract class Mirror extends OctoObjectRule
{
	private static final long serialVersionUID = -2328141171759693459L;
	private final String mirror_attribute;
	private final String mirror_name_match;
	private final Object mirror_value_match;

	protected Mirror(String mirror_attribute, String mirror_name_match, Object mirror_value_match)
	{
		super(mirror_attribute);
		this.mirror_attribute = mirror_attribute;
		this.mirror_name_match = mirror_name_match;
		this.mirror_value_match = mirror_value_match;
	}

	@Override
	public final Object Compute(ModelObject object)
	{
		return object.GetInNeighbors().append(object.GetOutNeighbors())
			.Filter(mirror_name_match, mirror_value_match)
			.Only().GetAttribute(mirror_attribute).GetValue();
	}

	@Override
	public AttributeList<IMetaAttribute> GetDependency(ModelObject object)
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		result.add(
			object.GetInNeighbors().append(object.GetOutNeighbors())
			.Filter(mirror_name_match, mirror_value_match)
			.Only().GetMetaAttribute(mirror_attribute));

		return result;
	}}
