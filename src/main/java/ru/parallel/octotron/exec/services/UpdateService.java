package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.services.workers.Updater;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class UpdateService extends Service
{
	/**
	 * single threaded pool
	 * it processes sensors import, varyings modification and, reactions processing
	 * the only pool, that modifies the model
	 * */
	private final ThreadPoolExecutor update_executor;
	private final ReactionService reaction_service;

	public UpdateService(Context context, ReactionService reaction_service)
	{
		super(context);
		this.reaction_service = reaction_service;

		update_executor = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	}

	public void Update(SensorAttribute sensor, boolean check_reactions)
	{
		update_executor.execute(new Updater(reaction_service, sensor, check_reactions));
		context.stat.Add("update_executor", 1, update_executor.getQueue().size());
	}


	@Override
	public void Finish()
	{
		ShutdownExecutor(update_executor);
	}
}
