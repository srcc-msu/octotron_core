/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueID<T> implements IUniqueID<T>
{
	private static AtomicLong static_id = new AtomicLong(0);

	private final long id;
	private final T type;

	public UniqueID(long id, T type)
	{
		this.type = type;
		this.id = id;
	}

	public UniqueID(T type)
	{
		this.type = type;
		this.id = static_id.incrementAndGet();
	}

	@Override
	public long GetID()
	{
		return id;
	}

	@Override
	public T GetType()
	{
		return type;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof UniqueID))
			return false;

		UniqueID<?> cmp = ((UniqueID<?>)object);

		return id == cmp.id && type.equals(cmp.type);
	}
}
