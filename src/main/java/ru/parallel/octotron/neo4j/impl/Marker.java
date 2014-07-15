/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.neo4j.impl;

public class Marker
{
	private final long id;
	private final long reaction_id;
	private final String description;
	private final boolean suppress;

	public Marker(long id, long reaction_id, String description, boolean suppress)
	{
		this.id = id;
		this.reaction_id = reaction_id;
		this.suppress = suppress;
		this.description = description;
	}

	public long GetTarget()
	{
		return reaction_id;
	}

	public String GetDescription()
	{
		return description;
	}

	public boolean IsSuppress()
	{
		return suppress;
	}

	public long GetID()
	{
		return id;
	}
}
