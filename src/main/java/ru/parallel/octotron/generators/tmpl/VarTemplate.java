/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.logic.Rule;

public class VarTemplate
{
	public final String name;
	public final Rule rule;

	public VarTemplate(String name, Rule rule)
	{
		this.name = name;
		this.rule = rule;
	}
}
