/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.persistence.IPersistenceService;

public class DummyPersistenceService implements IPersistenceService
{
	public DummyPersistenceService() {}

	@Override
	public void MakeRuleDependency(final ModelEntity entity) {}

	@Override
	public void RegisterObject(ModelObject object) {}
	@Override
	public void RegisterLink(ModelLink link) {}
	@Override
	public void RegisterConst(Const attribute) {}
	@Override
	public void RegisterSensor(Sensor attribute) {}
	@Override
	public void RegisterVar(Var attribute) {}
	@Override
	public void RegisterTrigger(Trigger trigger) {}
	@Override
	public void RegisterReaction(Reaction reaction) {}
	@Override
	public void Operate() {}
	@Override
	public void Finish() {}
	@Override
	public void Clean() {}
	@Override
	public void Check() {}
}
