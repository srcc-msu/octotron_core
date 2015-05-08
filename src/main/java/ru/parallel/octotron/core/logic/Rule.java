/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelEntity;

/**
 * currently Octopy supports only only one constructor for rules</br>
 * */
public abstract class Rule
{
	protected Rule(){}

	public abstract Object Compute(ModelEntity entity);

	public abstract AttributeList<Attribute> GetDependency(ModelEntity entity);
}
