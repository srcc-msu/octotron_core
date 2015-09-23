/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class BGExecutorService implements Executor
{
	protected final static Logger LOGGER = Logger.getLogger("octotron");

	private final String prefix;
	private final ThreadPoolExecutor executor;

	// blocks the submitting thread if the queue reaches the limit
	// 0 - no blocking
	private long max_waiting = 0l;

	public BGExecutorService(String prefix, int core_pool_size, int maximum_pool_size
		, long keep_alive_ms, BlockingQueue<Runnable> queue, long max_waiting)
	{
		this.prefix = prefix;
		executor = new ThreadPoolExecutor(core_pool_size, maximum_pool_size
			, keep_alive_ms, TimeUnit.MILLISECONDS, queue);

		executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("bg_service_" + prefix + "_%d").build());

		this.max_waiting = max_waiting;
	}

	/**
	 * simple single threaded
	 * */
	public BGExecutorService(String prefix, long max_waiting)
	{
		this(prefix, 1, 1, 0L, new LinkedBlockingQueue<Runnable>(), max_waiting);
	}

	public BGExecutorService(String prefix, int threads, long max_waiting)
	{
		this(prefix, threads, threads, 0L, new LinkedBlockingQueue<Runnable>(), max_waiting);
	}

	public final String GetName()
	{
		return prefix;
	}

	public void execute(Runnable command)
	{
		if(max_waiting > 0 && GetWaitingCount() > max_waiting)
		{
			LOGGER.log(Level.INFO, prefix + " executor is full, blocking the calling thread");
			WaitTasks(max_waiting);
		}

		executor.execute(command);
	}

	public void SetMaxWaiting(int max_waiting)
	{
		this.max_waiting = max_waiting;
	}

	public final int GetWaitingCount()
	{
		return executor.getQueue().size();
	}

	public final long GetCompletedCount()
	{
		return executor.getCompletedTaskCount();
	}

	public final long GetTaskCount()
	{
		return executor.getTaskCount();
	}

	private long completed_last = 0;

	public final long GetRecentCompletedCount()
	{
		long completed = GetCompletedCount();
		long result = completed - completed_last;

		completed_last = completed;

		return result;
	}

	public void WaitAllTasks()
	{
		WaitTasks(0);
	}

	public void WaitTasks(long count)
	{
		while(GetTaskCount() - GetCompletedCount() > count)
		{
			try { Thread.sleep(1); }
			catch(InterruptedException ignore) {} // NOBODY DARES TO INTERRUPT ME
		}
	}

	public void Finish()
	{
		// silently ignore all new requests
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		WaitAllTasks();
		ShutdownExecutor(executor);
	}

	/**
	 * ensure that there wil be only one thread
	 * */
	public void LockOnThread()
	{
		executor.prestartCoreThread();

		executor.setThreadFactory(
			new ThreadFactory()
			{
				@Override
				public Thread newThread(Runnable r)
				{
					throw new ExceptionModelFail("db thread failed, refusing to create next");
				}
			});
	}
}
