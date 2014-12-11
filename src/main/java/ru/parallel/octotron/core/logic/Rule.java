/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.services.ModelService;

/**
 * currently Octopy supports only only one constructor for rules</br>
 * */
public abstract class Rule
{
	protected Rule(){}

	public abstract Object Compute(ModelEntity entity);

	public AttributeList<IModelAttribute> GetDependency(ModelService service, ModelEntity entity)
	{
		service.CheckModification();

		return GetDependency(entity);
	}

	protected abstract AttributeList<IModelAttribute> GetDependency(ModelEntity entity);

	public boolean CanCompute(VarAttribute var)
	{
		for(IModelAttribute attribute : var.GetIDependOn())
			if(!attribute.Check())
				return false;

		return true;
	}
}
