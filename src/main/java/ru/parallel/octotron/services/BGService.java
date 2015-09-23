/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services;

import ru.parallel.octotron.exec.Context;

public abstract class BGService extends Service
{
	protected final BGExecutorService executor;

	public static final int DEFAULT_QUEUE_LIMIT = 1000000;

	public BGService(Context context, BGExecutorService executor)
	{
		super(context);
		this.executor = executor;
		ServiceLocator.INSTANCE.GetRuntimeService().GetStat().RegisterService(executor);
	}

	public void Finish()
	{
		executor.Finish();
	}

	public BGExecutorService GetExecutor()
	{
		return executor;
	}

	public void SetMaxWaiting(int max_waiting)
	{
		executor.SetMaxWaiting(max_waiting);
	}
}
