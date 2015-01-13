package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.Context;
import ru.parallel.utils.JavaUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class OutdatedCheckerService extends Service
{
	static private final Object lock = new Object();

	private final UpdateService update_service;

	class Checker implements Runnable
	{
		@Override
		public void run()
		{
			synchronized(lock)
			{
				while(true)
				{
					try
					{
						Thread.sleep(1000);
						lock.wait();
					}
					catch(InterruptedException e)
					{
						LOGGER.log(Level.WARNING, "outdated thread interrupted");
						return;
					}

					Collection<SensorAttribute> outdated_sensors = ProcessOutdatedSensors(context);

					for(SensorAttribute sensor : outdated_sensors)
						update_service.Update(sensor, true);

					if(outdated_sensors.size() > 0)
						LOGGER.log(Level.INFO, "outdated sensors: " + outdated_sensors.size());
				}
			}
		}
	}

	private final Thread checker = new Thread(new Checker());

	public OutdatedCheckerService(Context context, UpdateService update_service)
	{
		super(context);
		this.update_service = update_service;

		checker.setName("outdated_checker");
		checker.start();
	}

	public static Collection<SensorAttribute> ProcessOutdatedSensors(Context context)
	{
		List<SensorAttribute> outdated_sensors = new LinkedList<>();

		long cur_time = JavaUtils.GetTimestamp();

		for(ModelEntity entity : context.model_data.GetAllEntities())
		{
			for(SensorAttribute sensor : entity.GetSensor())
			{
				boolean last_state = sensor.IsOutdated();

				boolean new_state = sensor.UpdateIsOutdated(cur_time);

				if(new_state && !last_state) // was not outdated, but now is
					outdated_sensors.add(sensor);
			}
		}

		return outdated_sensors;
	}

	public void PerformCheck()
	{
		synchronized(lock)
		{
			lock.notify();
		}
	}

	@Override
	public void Finish()
	{
		LOGGER.log(Level.INFO, "interrupting outdated checker");
		checker.interrupt();
	}
}
