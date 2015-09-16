/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.logic.Rule;

public class TriggerTemplate
{
	public String name;
	public Rule condition;

	public TriggerTemplate(String name, Rule condition)
	{
		this.name = name;
		this.condition = condition;
	}
}
