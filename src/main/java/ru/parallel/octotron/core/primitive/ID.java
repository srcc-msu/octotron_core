/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

public class ID<T> implements IUniqueID<T>
{
	private final long id;
	private final T type;

	public ID(long id, T type)
	{
		this.type = type;
		this.id = id;
	}

	@Override
	public final long GetID()
	{
		return id;
	}

	@Override
	public final T GetType()
	{
		return type;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof ID))
			return false;

		ID<?> cmp = ((ID<?>)object);

		return id == cmp.id && type.equals(cmp.type);
	}
}
