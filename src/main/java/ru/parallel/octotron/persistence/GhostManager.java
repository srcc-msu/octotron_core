/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence;

import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

public class GhostManager implements IPersistenceManager
{
	@Override
	public void RegisterObject(ModelObject object)
	{
		// there is nothing here
	}

	@Override
	public void RegisterLink(ModelLink link)
	{
		// there is nothing here
	}

	@Override
	public void RegisterReaction(Reaction reaction)
	{
		// there is nothing here
	}

	@Override
	public void RegisterConst(Const attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterSensor(Sensor attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterVar(Var attribute)
	{
		// there is nothing here
	}

	@Override
	public void Finish()
	{
		// there is nothing here
	}

	@Override
	public void MakeRuleDependency(ModelEntity entity)
	{
		// there is nothing here
	}

	@Override
	public void Wipe()
	{
		// there is nothing here
	}

	@Override
	public void RegisterTrigger(Trigger attribute)
	{
		// there is nothing here
	}
}
