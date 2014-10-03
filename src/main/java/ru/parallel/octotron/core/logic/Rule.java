/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;


import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

/**
 * currently Octopy supports only only one constructor for rules</br>
 * */
public abstract class Rule
{
	protected final String name;

	protected Rule(String name)
	{
		this.name = name;
	}

	protected Rule()
	{
		throw new ExceptionModelFail("super(attribute_name) must be called in the constructor");
	}


	public final String GetName()
	{
		return name;
	}

	public abstract Object Compute(ModelEntity entity);
	public abstract Object GetDefaultValue();

	public abstract AttributeList<IModelAttribute> GetDependency(ModelEntity entity);
}
