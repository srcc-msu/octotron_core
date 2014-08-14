/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.impl;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoRule;

public final class PersistentStorage
{
	public static final PersistentStorage INSTANCE = new PersistentStorage();

	private final PersistentMap<OctoRule> rules = new PersistentMap<>();
	private final PersistentMap<OctoReaction> reactions = new PersistentMap<>();

	private PersistentStorage() {}

	public void Load(String fname)
		throws ExceptionSystemError
	{
		try
		{
			rules.Load(fname + ".rule");
			reactions.Load(fname + ".react");
		}
		catch(Exception e)
		{
			throw new ExceptionSystemError("error loading persistent storage: "
				+ e);
		}
	}

	public void Save(String fname)
		throws ExceptionSystemError
	{
		try
		{
			rules.Save(fname + ".rule");
			reactions.Save(fname + ".react");
		}
		catch (Exception e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	public PersistentMap<OctoRule> GetRules()
	{
		return rules;
	}


	public PersistentMap<OctoReaction> GetReactions()
	{
		return reactions;
	}
}
