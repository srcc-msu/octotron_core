/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.rules;

public class MirrorBoolean extends Mirror
{

	public MirrorBoolean(String mirror_attribute, String mirror_name_match, Object mirror_value_match)
	{
		super(mirror_attribute, mirror_name_match, mirror_value_match);
	}

	@Override
	public Object GetDefaultValue()
	{
		return true;
	}
}
