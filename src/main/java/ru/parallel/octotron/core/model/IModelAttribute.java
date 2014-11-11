/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.IAttributeBuilder;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.primitive.IUniqueID;

import java.util.Collection;

public interface IModelAttribute extends IUniqueID<EAttributeType>, IAttribute, IPresentable
{
	boolean Check();

	Reaction GetReaction(long id);
	Collection<Reaction> GetReactions();

	double GetSpeed();

	AttributeList<VarAttribute> GetDependant();

	IAttributeBuilder GetBuilder(ModelService service);
}
