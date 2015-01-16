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
import ru.parallel.utils.JavaUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutionController
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public final Context context;

	public final ModelService model_service;

	private final RequestService request_service;
	private final HttpService http_service;

	private final ReactionService reaction_service;
	private final UpdateService update_service;
	private final ImportService import_service;

	private final OutdatedCheckerService checker_service;

	private boolean exit = false;

	public ExecutionController(Context context, ModelService model_service)
		throws ExceptionSystemError
	{
		this.context = context;
		this.model_service = model_service;

		request_service = new RequestService("requests", context, this);
		http_service = new HttpService("http_requests", context, request_service);

		reaction_service = new ReactionService("reactions", context, model_service.GetPersistenceService());
		update_service = new UpdateService("updates", context, reaction_service, model_service.GetPersistenceService());
		import_service = new ImportService("imports", context, update_service);

		checker_service = new OutdatedCheckerService(context, update_service);

		UpdateDefinedSensors();

		model_service.GetPersistenceService().GetExecutor().SetMaxWaiting(BGService.DEFAULT_QUEUE_LIMIT);
	}

	public UpdateService GetUpdateService()
	{
		return update_service;
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

		context.stat.Process();

		Thread.sleep(1);
	}

	public void Finish()
	{
		LOGGER.log(Level.INFO, "waiting for all tasks to finish");

		http_service.Finish();
		request_service.Finish();

		import_service.Finish();
		update_service.Finish();
		reaction_service.Finish();

		checker_service.Finish();

		LOGGER.log(Level.INFO, "all processing finished");
	}


	public Context GetContext()
	{
		return context;
	}

	public void SetSilent(boolean mode)
	{
		reaction_service.SetSilent(mode);
	}

	public ImportService GetImportService()
	{
		return import_service;
	}
}
