/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

public class Uid
{
	private long uid;
	private EEntityType type;

	public Uid(long uid, EEntityType type)
	{
		this.uid = uid;
		this.type = type;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof Uid))
			return false;

		Uid cmp = ((Uid)object);

		return uid == cmp.uid && type == cmp.type;
	}

	public long getUid()
	{
		return uid;
	}

	public EEntityType getType()
	{
		return type;
	}
}
