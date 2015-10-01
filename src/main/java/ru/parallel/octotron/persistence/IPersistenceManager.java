/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence;

import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

public interface IPersistenceManager
{
	void RegisterObject(ModelObject object);
	void RegisterLink(ModelLink link);

	void RegisterConst(Const attribute);
	void RegisterSensor(Sensor attribute);
	void RegisterVar(Var attribute);
	void RegisterTrigger(Trigger trigger);

	void RegisterReaction(Reaction reaction);
	void MakeRuleDependency(ModelEntity entity);
}
