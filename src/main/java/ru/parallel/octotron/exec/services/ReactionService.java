package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.impl.Reaction;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.reactions.PreparedResponseFactory;

public class ReactionService extends BGService
{
	private final PreparedResponseFactory response_factory;

	/**
	 * silent mode = no responses will be invoked
	 * */
	private boolean silent;

	public ReactionService(String prefix, Context context, PersistenceService persistence_service)
	{
		super(context, new BGExecutorService(prefix, context.settings.GetNumThreads()
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

			if(response.GetStatus() != EEventStatus.RECOVER)
				reaction.RegisterPreparedResponse(prepared_response);

			if(!IsSilent())
				prepared_response.run();
		}
	}
}
