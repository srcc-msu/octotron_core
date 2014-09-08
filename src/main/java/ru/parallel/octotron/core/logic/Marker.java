/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.primitive.UniqueName;

public class Marker implements UniqueName
{
	private final long reaction_id;
	private final String description;
	private final boolean suppress;

	public Marker(long reaction_id, String description, boolean suppress)
	{
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

	@Override
	public String GetUniqName()
	{
		return Long.toString(reaction_id);
	}
}
