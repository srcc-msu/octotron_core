/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.DynamicSleeper;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides background execution of all needed reactions
 * */
public class ReactionInvoker
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final GlobalSettings settings;
	private final ExecutorService executor;

	public ReactionInvoker(GlobalSettings settings, ExecutorService executor)
	{
		this.settings = settings;
		this.executor = executor;
	}

	public void Invoke(List<PreparedResponse> responses, boolean silent)
	{
		if(silent)
		{
			if(!responses.isEmpty())
				LOGGER.log(Level.INFO, "silent mode, reactions ignored: " + responses.size());

			return;
		}

		for(PreparedResponse response : responses)
			executor.execute(response);
	}

	/*public void Invoke(AttributeList<IModelAttribute> changed, boolean silent)
	{
		List<PreparedResponse> responses = new LinkedList<>();

		for(IModelAttribute attribute : changed)
			for(Response response : attribute.ProcessReactions())
			{
				responses.add(new PreparedResponse(response, attribute.GetParent(), JavaUtils.GetTimestamp()));
			}

		if(!silent)
			pending_response.addAll(responses);
		else if(!responses.isEmpty())
			LOGGER.log(Level.INFO, "silent mode, reactions ignored: " + responses.size());
	}*/
}
