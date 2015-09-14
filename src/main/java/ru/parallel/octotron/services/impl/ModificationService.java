package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.attributes.impl.Trigger;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.logging.Level;

public class ModificationService extends BGService
{
	public ModificationService(Context context)
	{
		super(context, new BGExecutorService("modification", 10000000));
	}

	public boolean Import(ModelEntity entity, String name, Value value, boolean strict)
		throws ExceptionSystemError
	{
		if(entity.TestAttribute(name))
		{
			Import(entity.GetSensor(name), value);
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

	public void Import(Sensor sensor, Value value)
	{
		executor.execute(new Importer(sensor, value));
	}

	public void UnknownImport(ModelEntity target, String name, Value value)
		throws ExceptionSystemError
	{
		String script = context.settings.GetScriptByKeyOrNull("on_new_attribute");

		if(script == null) // processing is disabled
			return;

		try
		{
			ServiceLocator.INSTANCE.GetScriptService().ExecSilent(script
				, Long.toString(target.GetID()), name, value.toString());
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not execute script: " + script, e);
			throw e;
		}
	}

	public void Activate(ModelEntity entity, String name)
	{
		executor.execute(new Activator(entity, name));
	}

	public void CheckOutdated(ModelEntity entity)
	{
		executor.execute(new OutdatedChecker(entity));
	}

	public class Importer implements Runnable
	{
		protected final Sensor sensor;
		protected final Value value;

		public Importer(Sensor sensor, Value value)
		{
			this.sensor = sensor;
			this.value = value;
		}

		@Override
		public void run()
		{
			sensor.UpdateValue(value);
		}
	}

	public class Activator implements Runnable
	{
		protected final ModelEntity entity;
		protected final String name;

		public Activator(ModelEntity entity, String name)
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

	public class OutdatedChecker implements Runnable
	{
		protected final ModelEntity entity;

		public OutdatedChecker(ModelEntity entity)
		{
			this.entity = entity;
		}

		@Override
		public void run()
		{
			long outdated_count = 0;

			for(Sensor sensor : entity.GetSensor())
			{
				boolean last_state = sensor.IsOutdated();

				sensor.UpdateSelf();

				if(sensor.IsOutdated() && !last_state) // was not outdated, but now is
					outdated_count++;
			}
		}
	}
}
