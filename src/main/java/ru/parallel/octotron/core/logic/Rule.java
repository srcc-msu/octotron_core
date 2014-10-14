/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;


import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

/**
 * currently Octopy supports only only one constructor for rules</br>
 * */
public abstract class Rule
{
	protected Rule(){}

	public abstract Object Compute(ModelEntity entity);
	public abstract Object GetDefaultValue();

	public abstract AttributeList<IModelAttribute> GetDependency(ModelEntity entity);
}
