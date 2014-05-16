/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.neo4j.impl;

import main.java.ru.parallel.octotron.impl.PersistenStorage;

public class Marker implements java.io.Serializable
{
	private static final long serialVersionUID = -6782296632908542424L;

	private long AID;
	private long marker_id;
	private long reaction_id;

	private String description;

	private boolean suppress;

	public Marker(long AID, long reaction_id, String description, boolean suppress)
	{
		this.AID = AID;
		this.reaction_id = reaction_id;
		this.suppress = suppress;
		this.description = description;

		Register();
	}

	private void Register()
	{
		marker_id = PersistenStorage.INSTANCE.GetMarkers().Add(this);
	}

	public long GetAID()
	{
		return AID;
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
		return marker_id;
	}

}
