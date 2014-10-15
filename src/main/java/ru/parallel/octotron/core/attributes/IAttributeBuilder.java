/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.ReactionTemplate;

public interface IAttributeBuilder
{
	void AddReaction(ReactionTemplate reaction);
	void AddDependant(VarAttribute attribute);
}
