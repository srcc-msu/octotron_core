package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class BGService extends Service
{
	private final String prefix;
	protected final ThreadPoolExecutor executor;

	public BGService(String prefix, Context context, int core_pool_size, int maximum_pool_size
		, long keep_alive_ms, BlockingQueue<Runnable> queue)
	{
		super(context);
		this.prefix = prefix;
		executor = new ThreadPoolExecutor(core_pool_size, maximum_pool_size
			, keep_alive_ms, TimeUnit.MILLISECONDS, queue);
	}

	public final String GetName()
	{
		return prefix;
	}

	/**
	 * simple single threaded
	 * */
	public BGService(String prefix, Context context)
	{
		this(prefix, context, 1, 1, 0L, new LinkedBlockingQueue<Runnable>());
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

	@Override
	public void Finish()
	{
		ShutdownExecutor(executor);
	}
}
