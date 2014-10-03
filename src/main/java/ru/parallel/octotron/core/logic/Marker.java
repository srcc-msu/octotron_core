/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;

public class Marker extends UniqueID<EEntityType>
{
	private final Reaction reaction;
	private final String description;
	private final boolean suppress;

	public Marker(Reaction reaction, String description, boolean suppress)
	{
		super(EEntityType.MARKER);

		this.reaction = reaction;
		this.suppress = suppress;
		this.description = description;
	}

	public Reaction GetReaction()
	{
		return reaction;
	}

	public String GetDescription()
	{
		return description;
	}

	public boolean IsSuppress()
	{
		return suppress;
	}

}
