/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services;

import ru.parallel.octotron.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.bg_services.db.IPersistenceService;
import ru.parallel.octotron.bg_services.db.DummyPersistenceService;
import ru.parallel.octotron.bg_services.db.Neo4jPersistenceService;
import ru.parallel.octotron.bg_services.model.*;
import ru.parallel.octotron.bg_services.side.HttpService;
import ru.parallel.octotron.bg_services.side.ScriptService;
import ru.parallel.octotron.services.ModelService;
import ru.parallel.octotron.services.SystemService;

import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: kill myself for adding this thing. i was wrong
public class ServiceLocator
{
	// TODO: remove singleton
	public static ServiceLocator INSTANCE;

	private final static Logger LOGGER = Logger.getLogger("octotron");

	private Context context;

	private ModelService model_service;
	private BlockingRequestService blocking_request_service;
	private RequestService request_service;
	private HttpService http_service;
	private ReactionService reaction_service;
	private ModificationService modification_service;
	private OutdatedCheckerService checker_service;
	private IPersistenceService persistence_service;
	private ScriptService script_service;
	private SystemService runtime_service;

	public ServiceLocator(Context context)
	{
		this.context = context;
	}

	public IPersistenceService GetPersistenceService()
	{
		if(persistence_service == null)
		{
			if(context.settings.IsDb())
				persistence_service = new Neo4jPersistenceService(context, GetModelService().GetMode());
			else
				persistence_service = new DummyPersistenceService();
		}

		return persistence_service;
	}

	public ModelService GetModelService()
	{
		if(model_service == null)
			try
			{
				model_service = new ModelService(context);
			}
			catch(ExceptionSystemError e)
			{
				LOGGER.log(Level.SEVERE, "could not create model service", e);
			}

		return model_service;
	}

	public RequestService GetRequestService()
	{
		if(request_service == null)
			request_service = new RequestService(context);

		return request_service;
	}

	public BlockingRequestService GetBlockingRequestService()
	{
		if(blocking_request_service == null)
			blocking_request_service = new BlockingRequestService(context);

		return blocking_request_service;
	}

	public HttpService GetHttpService()
	{
		if(http_service == null)
			try
			{
				http_service = new HttpService(context);
			}
			catch(ExceptionSystemError e)
			{
				LOGGER.log(Level.SEVERE, "could not create http service", e);
			}

		return http_service;
	}

	public ReactionService GetReactionService()
	{
		if(reaction_service == null)
			reaction_service = new ReactionService(context);

		return reaction_service;
	}

	public ModificationService GetModificationService()
	{
		if(modification_service == null)
			modification_service = new ModificationService(context);
		return modification_service;
	}

	public OutdatedCheckerService GetOutdatedCheckerService()
	{
		if(checker_service == null)
			checker_service = new OutdatedCheckerService(context);
		return checker_service;
	}

	public ScriptService GetScriptService()
	{
		if(script_service == null)
			script_service = new ScriptService(context);
		return script_service;
	}

	public SystemService GetRuntimeService()
	{
		if(runtime_service == null)
			runtime_service = new SystemService(context);
		return runtime_service;
	}

	public void Finish()
	{
		if(request_service      != null) request_service.Finish();
		if(http_service         != null) http_service.Finish();
		if(reaction_service     != null) reaction_service.Finish();
		if(modification_service != null) modification_service.Finish();
		if(checker_service      != null) checker_service.Finish();
		if(persistence_service  != null) persistence_service.Finish();
		if(script_service       != null) script_service.Finish();
	}
}
