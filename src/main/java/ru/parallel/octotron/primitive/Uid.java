/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.primitive;

public class Uid
{
	private long uid;
	private EEntityType type;

	public Uid(long uid, EEntityType type)
	{
		this.uid = uid;
		this.type = type;
	}

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public EEntityType getType()
	{
		return type;
	}

	public void setType(EEntityType type)
	{
		this.type = type;
	}
}
