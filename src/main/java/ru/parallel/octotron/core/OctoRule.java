/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.impl.PersistentStorage;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

import java.io.Serializable;

/**
 * currently Octopy supports only only one constructor for rules</br>
 * */
public abstract class OctoRule implements Serializable
{
	private static final long serialVersionUID = 6126662649331847764L;
	protected final String name;
	private long rule_id;

	protected OctoRule(String name)
	{
		this.name = name;
		rule_id = PersistentStorage.INSTANCE.GetRules().Add(this);
	}

	protected OctoRule()
	{
		throw new ExceptionModelFail("super(attribute_name) must be called in the constructor");
	}

	public final long GetID()
	{
		return rule_id;
	}

	public final String GetAttribute()
	{
		return name;
	}

	public abstract Object Compute(OctoEntity entity);
	public abstract Object GetDefaultValue();
	public abstract EDependencyType GetDependency();

}
