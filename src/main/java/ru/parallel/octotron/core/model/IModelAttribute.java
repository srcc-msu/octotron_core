package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.EAttributeType;
import ru.parallel.octotron.core.attributes.IAttributeBuilder;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.Collection;

public interface IModelAttribute extends IAttribute
{
	EAttributeType GetType();

	boolean IsValid();
	void SetValid();
	void SetInvalid();

	Reaction GetReaction(long id);
	Collection<Reaction> GetReactions();

	double GetSpeed();

	AttributeList<VarAttribute> GetDependant();
	Collection<PreparedResponse> ProcessReactions();

	IAttributeBuilder GetBuilder();
}
