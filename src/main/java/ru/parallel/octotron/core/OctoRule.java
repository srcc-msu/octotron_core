/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import main.java.ru.parallel.octotron.impl.PersistenStorage;
import main.java.ru.parallel.octotron.primitive.EDependencyType;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

import java.io.Serializable;

public abstract class OctoRule implements Serializable
{
	private static final long serialVersionUID = 6126662649331847764L;
	protected final String attr;
	private long rule_id;

	public OctoRule(String attr)
	{
		this.attr = attr;
		Register();
	}

	private void Register()
	{
		rule_id = PersistenStorage.INSTANCE.GetRules().Add(this);
	}

	public final long GetID()
	{
		return rule_id;
	}

	public Object Compute(OctoObject object)
	{
		throw new ExceptionModelFail
			("this rule is not applicable for objects");
	}

	public Object Compute(OctoLink link)
	{
		throw new ExceptionModelFail
			("this rule is not applicable for links");
	}

	public EDependencyType GetDeps()
	{
		return EDependencyType.ALL;
	}

	public final String GetAttr()
	{
		return attr;
	}

	public Object GetDefaultValue()
	{
		throw new ExceptionModelFail("default value for rule not specified");
	}
}
