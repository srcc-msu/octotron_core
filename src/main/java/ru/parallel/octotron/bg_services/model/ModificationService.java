/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services.model;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.attributes.impl.Trigger;
import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exception.ExceptionModelFail;
import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.octotron.bg_services.BGService;
import ru.parallel.octotron.bg_services.ServiceLocator;

import java.util.logging.Level;

public class ModificationService extends BGService
{
	public ModificationService(Context context)
	{
		super(context, new BGExecutorWrapper("modification", 10000000));
	}

	public boolean Import(ModelEntity entity, String name, Value value, long current_time, boolean strict)
		throws ExceptionSystemError
	{
		if(entity.TestAttribute(name))
		{
			Import(entity.GetSensor(name), value, current_time);
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

	public void Import(Sensor sensor, Value value, long current_time)
	{
		executor.execute(new Importer(sensor, value, current_time));
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
				, Long.toString(target.GetInfo().GetID()), name, value.toString());
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not execute script: " + script, e);
			throw e;
		}
	}

	public void Activate(ModelEntity entity, String name, long current_time)
	{
		executor.execute(new Activator(entity, name, current_time));
	}

	public void CheckOutdated(ModelEntity entity, long current_time)
	{
		executor.execute(new OutdatedChecker(entity, current_time));
	}

	public class Importer implements Runnable
	{
		protected final Sensor sensor;
		protected final Value value;
		protected final long current_time;

		public Importer(Sensor sensor, Value value, long current_time)
		{
			this.sensor = sensor;
			this.value = value;
			this.current_time = current_time;
		}

		@Override
		public void run()
		{
			sensor.Update(value, current_time);
		}
	}

	public class Activator implements Runnable
	{
		protected final ModelEntity entity;
		protected final String name;
		protected final long current_time;

		public Activator(ModelEntity entity, String name, long current_time)
		{
			this.entity = entity;
			this.name = name;
			this.current_time = current_time;
		}

		@Override
		public void run()
		{
			Trigger trigger = entity.GetTrigger(name);

			trigger.ForceTrigger(current_time);
		}
	}

	public class OutdatedChecker implements Runnable
	{
		protected final ModelEntity entity;
		protected final long current_time;

		public OutdatedChecker(ModelEntity entity, long current_time)
		{
			this.entity = entity;
			this.current_time = current_time;
		}

		@Override
		public void run()
		{
			long outdated_count = 0;

			for(Sensor sensor : entity.GetSensor())
			{
				boolean last_state = sensor.IsOutdated();

				sensor.UpdateSelf(current_time);

				if(sensor.IsOutdated() && !last_state) // was not outdated, but now is
					outdated_count++;
			}
		}
	}
}
