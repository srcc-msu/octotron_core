/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services.model;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.octotron.bg_services.BGService;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.utils.JavaUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OutdatedCheckerService extends BGService
{
	public OutdatedCheckerService(Context context)
	{
		super(context, new BGExecutorWrapper("outdated_checker", 1));
	}

	public static void ProcessOutdatedSensors(long current_time)
	{
		for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
		{
			ServiceLocator.INSTANCE.GetModificationService().CheckOutdated(entity, current_time);
		}
	}

	class Checker implements Runnable
	{
		protected final long current_time;

		public Checker(long current_time)
		{
			this.current_time = current_time;
		}

		@Override
		public void run()
		{
			LOGGER.log(Level.INFO, "starting outdated check");

			ProcessOutdatedSensors(current_time);

			LOGGER.log(Level.INFO, "outdated check finished");
		}
	}

	public boolean PerformCheck(long current_time)
	{
		if(executor.GetWaitingCount() > 0)
		{
			LOGGER.log(Level.WARNING, "outdated checker is still running, ignoring new task");
			return false;
		}

		executor.execute(new Checker(current_time));

		return true;
	}
}
