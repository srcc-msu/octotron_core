package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.exec.services.workers.Importer;
import ru.parallel.utils.FileUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

public class ImportService extends Service
{
	private final ThreadPoolExecutor import_executor;

	private final UpdateService update_service;

	public ImportService(Context context, UpdateService update_service)
	{
		super(context);
		this.update_service = update_service;

		import_executor = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	}

	public void ImmediateImport(ModelEntity entity, String name, Value value)
	{
		new Importer(update_service, entity, name, value).run();
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
		import_executor.execute(new Importer(update_service, entity, name, value));
		context.stat.Add("import_executor", 1, import_executor.getQueue().size());
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
		ShutdownExecutor(import_executor);
	}
}
