package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;

public abstract class BGService extends Service
{
	protected final BGExecutorService executor;

	public static final int DEFAULT_QUEUE_LIMIT = 100000;

	public BGService(Context context, BGExecutorService executor)
	{
		super(context);
		this.executor = executor;
		context.stat.RegisterService(executor);
	}

	public void Finish()
	{
		executor.Finish();
	}

	public BGExecutorService GetExecutor()
	{
		return executor;
	}
}
