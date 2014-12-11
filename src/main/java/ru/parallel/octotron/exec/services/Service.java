package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.exec.Context;

public abstract class Service
{
	protected final Context context;

	public Service(Context context)
	{
		this.context = context;
	}

	public abstract void Finish();
}
