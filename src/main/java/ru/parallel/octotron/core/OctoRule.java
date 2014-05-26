/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.impl.PersistenStorage;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

import java.io.Serializable;

public abstract class OctoRule implements Serializable
{
	private static final long serialVersionUID = 6126662649331847764L;
	protected final String attribute_name;
	private long rule_id;

	protected OctoRule(String attribute_name)
	{
		this.attribute_name = attribute_name;
		rule_id = PersistenStorage.INSTANCE.GetRules().Add(this);
	}

	protected OctoRule()
	{
		throw new ExceptionModelFail("super(attribute_name) must be called in the constructor");
	}

	public final long GetID()
	{
		return rule_id;
	}

	public final String GetAttr()
	{
		return attribute_name;
	}

	public abstract Object Compute(OctoEntity entity);
	public abstract Object GetDefaultValue();
	public abstract EDependencyType GetDeps();

}
