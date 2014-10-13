package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.ReactionTemplate;

public interface IAttributeBuilder
{
	void AddReaction(ReactionTemplate reaction);
	void AddDependant(VarAttribute attribute);
}
