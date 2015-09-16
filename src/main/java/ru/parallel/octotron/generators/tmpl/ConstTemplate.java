/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.impl.Value;

public class ConstTemplate
{
	public final String name;
	public final Value value;

	public ConstTemplate(String name, Object value)
	{
		this.name = name;
		this.value = Value.Construct(value);
	}
}
