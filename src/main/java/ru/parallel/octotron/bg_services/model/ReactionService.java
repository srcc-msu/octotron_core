/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.bg_services.model;

import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.reactions.PreparedResponseFactory;
import ru.parallel.octotron.bg_services.BGExecutorWrapper;
import ru.parallel.octotron.bg_services.BGService;

public class ReactionService extends BGService
{
	private final PreparedResponseFactory response_factory;

	/**
	 * silent mode = no responses will be invoked
	 * */
	private boolean silent;

	public ReactionService(Context context)
	{
		super(context, new BGExecutorWrapper("reactions", context.settings.GetNumThreads()
			, DEFAULT_QUEUE_LIMIT));

		this.response_factory = new PreparedResponseFactory(context);

		SetSilent(context.settings.IsStartSilent());
	}

	public boolean IsSilent()
	{
		return silent;
	}

	public void SetSilent(boolean silent)
	{
		this.silent = silent;
	}

//--------

	public void AddResponse(Reaction reaction, Response response)
	{
		executor.execute(new Responder(reaction, response));
	}

	public void AddRecoverResponse(Reaction reaction, Response response)
	{
		executor.execute(new RecoverResponder(reaction, response));
	}

	public class Responder implements Runnable
	{
		private final Reaction reaction;
		private final Response response;

		public Responder(Reaction reaction, Response response)
		{
			this.reaction = reaction;
			this.response = response;
		}

		@Override
		public void run()
		{
			PreparedResponse prepared_response = response_factory
				.Construct(reaction.GetParent(), reaction, response);

			reaction.RegisterPreparedResponse(prepared_response);

			if(IsSilent())
				prepared_response.SetSuppress(true);

			prepared_response.run();
		}
	}

	public class RecoverResponder implements Runnable
	{
		private final Reaction reaction;
		private final Response response;

		public RecoverResponder(Reaction reaction, Response response)
		{
			this.reaction = reaction;
			this.response = response;
		}

		@Override
		public void run()
		{
			PreparedResponse prepared_response = response_factory
				.Construct(reaction.GetParent(), reaction, response);

			if(IsSilent())
				prepared_response.SetSuppress(true);

			prepared_response.run();
		}
	}
}
