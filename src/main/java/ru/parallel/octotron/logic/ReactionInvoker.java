/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.utils.OctoEntityList;
import ru.parallel.utils.DynamicSleeper;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * provides background execution of all needed reactions
 * */
public class ReactionInvoker
{
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
							System.err.println(e);
						}
					}
				}
				catch (InterruptedException ignore){}

				System.out.println("reaction invoker thread finished");
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

	public void Invoke(OctoEntityList all_changed, boolean silent)
	{
		OctoEntityList uniq_changed = all_changed.Uniq();

		List<PreparedResponse> new_responses = new LinkedList<>();

		for(OctoEntity entity : uniq_changed)
			for(OctoResponse response : entity.PreparePendingReactions())
			{
				new_responses.add(new PreparedResponse(response, entity, JavaUtils.GetTimestamp()));
			}

		if(!silent)
			pending_response.addAll(new_responses);
		else if(!new_responses.isEmpty())
			System.err.println("silent mode, reactions ignored: " + new_responses.size());
	}
}
