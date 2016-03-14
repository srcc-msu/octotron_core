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

import java.util.logging.Level;
import java.util.logging.Logger;

public class OutdatedCheckerService extends BGService
{
	public OutdatedCheckerService(Context context)
	{
		super(context, new BGExecutorWrapper("outdated_checker", 1));
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
