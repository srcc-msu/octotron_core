/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.primitive.Info;

import java.util.concurrent.atomic.AtomicLong;

/**
 * extends ID with a uniq identifiers generation for each new object
 * */
public class ModelInfo<T> extends Info<T>
{
	private static final AtomicLong static_id = new AtomicLong(0);

	public ModelInfo(T type)
	{
		super(static_id.incrementAndGet(), type);
	}
}
