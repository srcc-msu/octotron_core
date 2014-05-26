/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

import ru.parallel.octotron.primitive.SimpleAttribute;

public class MirrorString extends Mirror
{
	private static final long serialVersionUID = -409794818433135695L;

	public MirrorString(String mirror_attribute, SimpleAttribute mirror_parent)
	{
		super(mirror_attribute, mirror_parent);
	}

	public MirrorString(String mirror_attribute, String mirror_parent_name, Object mirror_parent_value)
	{
		this(mirror_attribute
			, new SimpleAttribute(mirror_parent_name, mirror_parent_value));
	}

	@Override
	public Object GetDefaultValue()
	{
		return "";
	}
}
