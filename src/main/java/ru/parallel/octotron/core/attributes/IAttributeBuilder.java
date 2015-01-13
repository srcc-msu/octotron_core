/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;

public interface IAttributeBuilder
{
	Reaction AddReaction(ReactionTemplate reaction);
	void AddDependant(VarAttribute attribute);
}
