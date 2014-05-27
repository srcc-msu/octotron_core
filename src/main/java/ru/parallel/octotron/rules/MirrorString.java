/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

public class MirrorString extends Mirror
{
	private static final long serialVersionUID = -409794818433135695L;

	public MirrorString(String mirror_attribute, String mirror_name_match, Object mirror_value_match)
	{
		super(mirror_attribute, mirror_name_match, mirror_value_match);
	}

	@Override
	public Object GetDefaultValue()
	{
		return "";
	}
}
