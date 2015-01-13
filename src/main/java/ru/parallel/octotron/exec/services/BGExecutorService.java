package ru.parallel.octotron.exec.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.concurrent.*;
import java.util.logging.Logger;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class BGExecutorService implements Executor
{
	protected final static Logger LOGGER = Logger.getLogger("octotron");

	private final String prefix;
	private final ThreadPoolExecutor executor;

	public BGExecutorService(String prefix, int core_pool_size, int maximum_pool_size
		, long keep_alive_ms, BlockingQueue<Runnable> queue)
	{
		this.prefix = prefix;
		executor = new ThreadPoolExecutor(core_pool_size, maximum_pool_size
			, keep_alive_ms, TimeUnit.MILLISECONDS, queue);

		executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("bg_service_" + prefix + "_%d").build());
	}

	public final String GetName()
	{
		return prefix;
	}

	public void execute(Runnable command)
	{
		executor.execute(command);
	}

	/**
	 * simple single threaded
	 * */
	public BGExecutorService(String prefix)
	{
		this(prefix, 1, 1, 0L, new LinkedBlockingQueue<Runnable>());
	}

	public final int GetWaitingCount()
	{
		return executor.getQueue().size();
	}

	public final long GetCompletedCount()
	{
		return executor.getCompletedTaskCount();
	}

	private long completed_last = 0;

	public final long GetRecentCompletedCount()
	{
		long completed = GetCompletedCount();
		long result = completed - completed_last;

		completed_last = completed;

		return result;
	}

	public void WaitAll()
	{
		while(GetWaitingCount() > 0)
		{
			try { Thread.sleep(1); }
			catch (InterruptedException ignore) {} // NOBODY DARES TO INTERRUPT ME
		}
	}

	public void Finish()
	{
		// silently ignore all new requests
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		WaitAll();
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
				private long created = 0;
				@Override
				public Thread newThread(Runnable r)
				{
					throw new ExceptionModelFail("db thread failed, refusing to create next");
				}
			});
	}
}
