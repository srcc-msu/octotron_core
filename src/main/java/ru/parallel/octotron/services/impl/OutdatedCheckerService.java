package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.Service;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.utils.JavaUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutdatedCheckerService extends BGService
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public OutdatedCheckerService(Context context)
	{
		super(context, new BGExecutorService("outdated_checker", 1));
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

			try { Thread.sleep(0, 100); } catch (InterruptedException ignore) {}
		}

		return outdated_sensors;
	}

	class Checker implements Runnable
	{
		@Override
		public void run()
		{
			Collection<Sensor> outdated_sensors = ProcessOutdatedSensors(context);

			LOGGER.log(Level.INFO, "outdated sensors: " + outdated_sensors.size());
		}
	}

	public void PerformCheck()
	{
		if(executor.GetWaitingCount() > 0)
		{
			LOGGER.log(Level.WARNING, "outdated checker is still running, ignoring new task");
			return;
		}

		executor.execute(new Checker());
	}
}
