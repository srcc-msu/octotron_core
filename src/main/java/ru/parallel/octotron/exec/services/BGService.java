package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;

public abstract class BGService extends Service
{
	protected final BGExecutorService executor;

	public BGService(Context context, BGExecutorService executor)
	{
		super(context);
		this.executor = executor;
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
