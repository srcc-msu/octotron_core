/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

import java.util.Collection;

public interface IPersistenceManager
{
	void RegisterObject(ModelObject object);
	void RegisterLink(ModelLink link);
	void RegisterReaction(Reaction reaction);
	void RegisterConst(ConstAttribute attribute);
	void RegisterSensor(SensorAttribute attribute);
	void RegisterVar(VarAttribute attribute);

	void Finish();

	void MakeRuleDependency(VarAttribute attribute);

	void RegisterUpdate(Collection<? extends IModelAttribute> attributes);

	void Wipe();
}
