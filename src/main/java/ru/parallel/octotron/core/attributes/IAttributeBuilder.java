package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.logic.Reaction;

public interface IAttributeBuilder
{
	void AddReaction(Reaction reaction);
	void AddDependant(VarAttribute attribute);
}
