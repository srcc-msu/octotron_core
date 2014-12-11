/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.services.*;
import ru.parallel.utils.FileUtils;
import ru.parallel.utils.JavaUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
		checker_service = new OutdatedCheckerService(context, reaction_service);
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

	public void Process()
		throws InterruptedException
	{
		if(JavaUtils.GetTimestamp() % OUTDATED_CHECK_INTERVAL == 0) // TODO: scheduler?
		{
			checker_service.PerformCheck();
		}

		if(!model_service.GetUpdateService().Update())
		{
			Thread.sleep(1);
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
