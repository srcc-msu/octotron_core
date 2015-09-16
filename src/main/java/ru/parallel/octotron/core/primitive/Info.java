/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

/**
 * provides a user level typing for objects with long identifiers
 * */
public class Info<T>
{
	private final long id;
	private final T type;

	public Info(long id, T type)
	{
		this.type = type;
		this.id = id;
	}

	public final long GetID()
	{
		return id;
	}

	public final T GetType()
	{
		return type;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof Info))
			return false;

		Info<?> cmp = ((Info<?>)object);

		return id == cmp.id && type.equals(cmp.type);
	}
}
