/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services;

import ru.parallel.octotron.exec.Context;

import java.util.logging.Logger;

public abstract class Service
{
	protected final static Logger LOGGER = Logger.getLogger("octotron");

	protected final Context context;

	public Service(Context context)
	{
		this.context = context;
	}

	public abstract void Finish();
}
