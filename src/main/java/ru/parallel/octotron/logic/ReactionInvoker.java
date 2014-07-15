/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.model.ModelAttribute;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * provides background execution of all needed reactions
 * */
public class ReactionInvoker
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Queue<PreparedResponse> pending_response
		= new ConcurrentLinkedQueue<>();

	private Thread invoker;

	private final GlobalSettings settings;

	public ReactionInvoker(GlobalSettings settings)
	{
		this.settings = settings;
		BackgroundInvoke();
	}

	private void BackgroundInvoke()
	{
		invoker = new Thread()
		{
			@Override
			public void run()
			{
				DynamicSleeper sleeper = new DynamicSleeper();

				try
				{
					while(true)
					{
						try
						{
							PreparedResponse response = pending_response.poll();

							if(response == null)
							{
								sleeper.Delay();
								sleeper.Sleep();
								continue;
							}

							response.Invoke(settings);
							sleeper.Act();
						}
						catch(ExceptionSystemError | ExceptionModelFail e)
						{
							LOGGER.log(Level.WARNING, "reaction invocation failed", e);
						}
					}
				}
				catch (InterruptedException ignore){}

				LOGGER.log(Level.INFO, "reaction invoker thread finished");
			}
		};

		invoker.setName("reaction_invoker");
		invoker.start();
	}

	public void Finish()
	{
		invoker.interrupt();
	}

	public boolean IsAlive()
	{
		return invoker.isAlive();
	}

	public void Invoke(AttributeList<ModelAttribute> changed, boolean silent)
	{
		List<PreparedResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : changed)
			for(OctoResponse response : attribute.PreparePendingReactions())
			{
				responses.add(new PreparedResponse(response, attribute.GetParent(), JavaUtils.GetTimestamp()));
			}

		if(!silent)
			pending_response.addAll(responses);
		else if(!responses.isEmpty())
			LOGGER.log(Level.INFO, "silent mode, reactions ignored: " + responses.size());
	}
}
