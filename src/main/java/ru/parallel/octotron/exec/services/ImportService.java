package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.attributes.impl.Trigger;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;

import java.util.logging.Level;

public class ImportService extends BGService
{
	public ImportService(String prefix, Context context)
	{
		super(context, new BGExecutorService(prefix, DEFAULT_QUEUE_LIMIT));
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
		executor.execute(new Importer(entity, name, value));
	}

	public void UnknownImport(ModelEntity target, String name, Value value)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKeyOrNull("on_new_attribute");

		if(script == null) // processing is disabled
			return;

		try
		{
			ScriptService.std.ExecSilent(script, Long.toString(target.GetID())
				, name, value.ValueToString());
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not execute script: " + script, e);
			throw e;
		}
	}

	public void Trigger(ModelEntity entity, String name)
		throws ExceptionSystemError
	{
		executor.execute(new Triggerer(entity, name));
	}

	public class Importer implements Runnable
	{
		protected final ModelEntity entity;
		protected final String name;
		protected final Value value;

		public Importer(ModelEntity entity, String name, Value value)
		{
			this.entity = entity;

			this.name = name;
			this.value = value;
		}

		@Override
		public void run()
		{
			Sensor sensor = entity.GetSensor(name);

			sensor.Import(value);
		}
	}

	public class Triggerer implements Runnable
	{
		protected final ModelEntity entity;
		protected final String name;

		public Triggerer(ModelEntity entity, String name)
		{
			this.entity = entity;

			this.name = name;
		}

		@Override
		public void run()
		{
			Trigger trigger = entity.GetTrigger(name);

			trigger.ForceTrigger();
		}
	}
}
