package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OutdatedCheckerService extends BGService
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public OutdatedCheckerService(Context context)
	{
		super(context, new BGExecutorService("outdated_checker", 1));
	}

	public static void ProcessOutdatedSensors()
	{
		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			ServiceLocator.INSTANCE.GetModificationService().CheckOutdated(entity);
		}
	}

	class Checker implements Runnable
	{
		@Override
		public void run()
		{
			LOGGER.log(Level.INFO, "starting outdated check");

			ProcessOutdatedSensors();

			LOGGER.log(Level.INFO, "outdated check finished");
		}
	}

	public boolean PerformCheck()
	{
		if(executor.GetWaitingCount() > 0)
		{
			LOGGER.log(Level.WARNING, "outdated checker is still running, ignoring new task");
			return false;
		}

		executor.execute(new Checker());

		return true;
	}
}
