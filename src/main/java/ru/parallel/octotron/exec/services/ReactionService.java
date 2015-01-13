package ru.parallel.octotron.exec.services;

import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.reactions.PreparedResponseFactory;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ru.parallel.utils.JavaUtils.ShutdownExecutor;

// TODO: executor?
public class ReactionService extends Service
{
	private final PersistenceService persistence_service;

	private final ThreadPoolExecutor reactions_executor;

	private final PreparedResponseFactory response_factory;

	/**
	 * silent mode = no responses will be invoked
	 * */
	private boolean silent = false;

	public ReactionService(Context context, PersistenceService persistence_service)
	{
		super(context);
		this.persistence_service = persistence_service;

		reactions_executor = new ThreadPoolExecutor(context.settings.GetNumThreads(), context.settings.GetNumThreads(),
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

		this.response_factory = new PreparedResponseFactory(context);
	}

	public boolean IsSilent()
	{
		return silent;
	}

	public void SetSilent(boolean silent)
	{
		this.silent = silent;
	}

// --------------

	private void AddResponse(PreparedResponse response)
	{
		if(IsSilent())
			return;

		context.stat.Add("reactions_executor", 1, reactions_executor.getQueue().size());

		reactions_executor.execute(response);
	}

	private void CheckSingleReaction(Reaction reaction)
	{
		Response response = reaction.ProcessOrNull();

		if(reaction.GetState() == Reaction.State.NONE)
			reaction.RegisterPreparedResponse(null);

		if(response == null)
			return;

		PreparedResponse prepared_response = response_factory
			.Construct(reaction.GetAttribute().GetParent(), reaction, response);

		if(response.GetStatus() != EEventStatus.RECOVER)
			reaction.RegisterPreparedResponse(prepared_response);

		AddResponse(prepared_response);
	}

	public void CheckReaction(Reaction reaction)
	{
		CheckSingleReaction(reaction);
		persistence_service.RegisterReaction(reaction);
	}

	public void CheckReactions(IModelAttribute attribute)
	{
		for(Reaction reaction : attribute.GetReactions())
		{
			CheckSingleReaction(reaction);
		}
		persistence_service.UpdateReactions(attribute.GetReactions()); // batch updating
	}

	public void CheckReactions(Collection<? extends IModelAttribute> attributes)
	{
		for(IModelAttribute attribute : attributes)
		{
			CheckReactions(attribute);
		}
	}

	public void Finish()
	{
		ShutdownExecutor(reactions_executor);
	}
}
