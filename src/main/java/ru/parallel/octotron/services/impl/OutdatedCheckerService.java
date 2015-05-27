package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.Service;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class OutdatedCheckerService extends Service
{
	static private final Object lock = new Object();

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

					Collection<Sensor> outdated_sensors = ProcessOutdatedSensors(context);

					if(outdated_sensors.size() > 0)
						LOGGER.log(Level.INFO, "outdated sensors: " + outdated_sensors.size());
				}
			}
		}
	}

	private final Thread checker = new Thread(new Checker());

	public OutdatedCheckerService(Context context)
	{
		super(context);

		checker.setName("outdated_checker");
		checker.start();
	}

	public static Collection<Sensor> ProcessOutdatedSensors(Context context)
	{
		List<Sensor> outdated_sensors = new LinkedList<>();

		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			for(Sensor sensor : entity.GetSensor())
			{
				boolean last_state = sensor.IsOutdated();

				sensor.UpdateSelf();

				if(sensor.IsOutdated() && !last_state) // was not outdated, but now is
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
