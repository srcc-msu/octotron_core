/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.services.*;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutionController
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public final Context context;

	public final ModelService model_service;

	public final RequestService request_service;
	public final HttpService http_service;
	public final UpdateService update_service;
	public final ReactionService reaction_service;

	public final OutdatedCheckerService checker_service;

	private boolean exit = false;

	public ExecutionController(Context context, ModelService model_service)
		throws ExceptionSystemError
	{
		this.context = context;
		this.model_service = model_service;

		request_service = new RequestService(context, this);
		http_service = new HttpService(context, request_service);
		reaction_service = new ReactionService(context);
		update_service = new UpdateService(context, reaction_service);
		checker_service = new OutdatedCheckerService(context, update_service);

		UpdateDefinedSensors();
	}

	private void UpdateDefinedSensors()
	{
		for(ModelObject object : context.model_data.GetAllObjects())
		{
			for(SensorAttribute sensor : object.GetSensor())
			{
				if(sensor.GetValue().IsDefined())
					update_service.Update(sensor, false);
			}
		}
	}

	public void SetExit(boolean exit)
	{
		this.exit = exit;
	}

	public boolean ShouldExit()
	{
		return exit;
	}

	private final long OUTDATED_CHECK_INTERVAL = 100;
	private long last_outdated_check = JavaUtils.GetTimestamp(); // do not run at start

	public void Process()
		throws InterruptedException
	{
		long current_time = JavaUtils.GetTimestamp();

		if(current_time - last_outdated_check > OUTDATED_CHECK_INTERVAL) // TODO: scheduler?
		{
			last_outdated_check = current_time;
			checker_service.PerformCheck();
		}

		// this processing must be in the same thread, that created neo4j... TODO
		if(!model_service.GetPersistenceService().Update()) // false = nothing to do
		{
			Thread.sleep(1); // TODO move to notify/wait or something
			return;
		}

		context.stat.Process();
	}

	public void Finish()
	{
		LOGGER.log(Level.INFO, "waiting for all tasks to finish");

		model_service.Finish();

		request_service.Finish();
		http_service.Finish();
		reaction_service.Finish();
		update_service.Finish();

		checker_service.Finish();

		FileUtils.Finish();

		LOGGER.log(Level.INFO, "all processing finished");
	}


	public Context GetContext()
	{
		return context;
	}
}
