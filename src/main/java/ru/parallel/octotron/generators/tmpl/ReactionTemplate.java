/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.generators.tmpl;

public class ReactionTemplate
{
	public final String name;
	public final ReactionAction action;

	public ReactionTemplate(String name, ReactionAction action)
	{
		this.name = name;
		this.action = action;
	}
}
