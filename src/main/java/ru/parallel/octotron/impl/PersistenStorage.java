/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.impl;

import main.java.ru.parallel.octotron.core.OctoReaction;
import main.java.ru.parallel.octotron.core.OctoRule;
import main.java.ru.parallel.octotron.neo4j.impl.Marker;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public final class PersistenStorage
{
	public static final PersistenStorage INSTANCE = new PersistenStorage();

	private final PersistentMap<OctoRule> rules = new PersistentMap<OctoRule>();
	private final PersistentMap<OctoReaction> reactions = new PersistentMap<OctoReaction>();
	private final PersistentMap<Marker> markers = new PersistentMap<Marker>();

	private PersistenStorage() {}

	public void Load(String fname)
		throws ExceptionSystemError
	{
		try
		{
			rules.Load(fname + ".rule");
			reactions.Load(fname + ".react");
			markers.Load(fname + ".mark");
		}
		catch (Exception e)
		{
			throw new ExceptionSystemError("error loading persistant storage: "
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
			markers.Save(fname + ".mark");
		}
		catch (Exception e)
		{
			throw new ExceptionSystemError("error saving persistant storage: "
				+ e.getMessage());
		}
	}

	public PersistentMap<OctoRule> GetRules()
	{
		return rules;
	}

	public PersistentMap<Marker> GetMarkers()
	{
		return markers;
	}

	public PersistentMap<OctoReaction> GetReactions()
	{
		return reactions;
	}
}
