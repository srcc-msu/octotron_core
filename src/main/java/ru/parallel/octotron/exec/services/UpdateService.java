package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.logic.Importer;
import ru.parallel.octotron.logic.Updater;
import ru.parallel.utils.FileUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class UpdateService extends Service
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

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

	public void ImmediateImport(ModelEntity entity, String name, Value value)
	{
		new Importer(reaction_service, entity, name, value).run();
	}

	public void Update(SensorAttribute sensor, boolean check_reactions)
	{
		update_executor.execute(new Updater(reaction_service, sensor, check_reactions));
		context.stat.Add("update_executor", 1, update_executor.getQueue().size());
	}

	public boolean Import(ModelEntity entity, String name, Value value, boolean strict)
		throws ExceptionSystemError
	{
		if(entity.TestAttribute(name))
		{
			Import(entity, name, value);
			return true;
		}
		else if(strict)
			throw new ExceptionModelFail("sensor does not exist: " + name);
		else
		{
			UnknownImport(entity, name, value);
			return false;
		}
	}

	public void Import(ModelEntity entity, String name, Value value)
	{
		update_executor.execute(new Importer(reaction_service, entity, name, value));
		context.stat.Add("update_executor", 1, update_executor.getQueue().size());
	}

	public void UnknownImport(ModelEntity target, String name, Value value)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKeyOrNull("on_new_attribute");

		try
		{
			FileUtils.ExecSilent(script, Long.toString(target.GetID())
				, name, value.ValueToString());
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not execute script: " + script, e);
			throw e;
		}
	}

	@Override
	public void Finish()
	{
		ShutdownExecutor(update_executor);
	}
}
