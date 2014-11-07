/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.primitive.ID;

import java.util.concurrent.atomic.AtomicLong;

public class ModelID<T> extends ID<T>
{
	private static AtomicLong static_id = new AtomicLong(0);

	public ModelID(T type)
	{
		super(static_id.incrementAndGet(), type);
	}
}
