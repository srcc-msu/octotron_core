/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.persistence;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;

import java.util.Collection;

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
	public void RegisterConst(ConstAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterSensor(SensorAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterVar(VarAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void Finish()
	{
		// there is nothing here
	}

	@Override
	public void MakeRuleDependency(VarAttribute attribute)
	{
		// there is nothing here
	}

	@Override
	public void RegisterUpdate(Collection<? extends IModelAttribute> attributes)
	{
		// there is nothing here
	}

	@Override
	public void Wipe()
	{
		// there is nothing here
	}
}
