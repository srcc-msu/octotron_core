/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelEntity;

/**
 * rules describe calculation of a new value basing on entity data(neighbours/attributes)
 * currently Octopy framework supports only only one constructor for rules
 * */
public abstract class Rule
{
	protected Rule(){}

	public abstract Object Compute(ModelEntity entity, Attribute rule_attribute);

	public abstract AttributeList<Attribute> GetDependency(ModelEntity entity);
}
