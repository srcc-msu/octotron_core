/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import ru.parallel.octotron.core.primitive.ID;

import java.util.concurrent.atomic.AtomicLong;

public class LogicID<T> extends ID<T>
{
	private static final AtomicLong static_id = new AtomicLong(0);

	public LogicID(T type)
	{
		super(static_id.incrementAndGet(), type);
	}
}
