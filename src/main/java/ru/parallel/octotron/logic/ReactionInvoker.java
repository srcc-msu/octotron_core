/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.DynamicSleeper;
import ru.parallel.utils.JavaUtils;

/**
 * provides background execution of all needed reactions
 * */
public class ReactionInvoker
{
	private final Queue<PreparedResponse> pending_response
		= new ConcurrentLinkedQueue<PreparedResponse>();

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

	public void Invoke(OctoObjectList all_changed, boolean silent)
	{
		OctoObjectList uniq_changed = all_changed.Uniq();

		List<PreparedResponse> new_responses = new LinkedList<PreparedResponse>();

		for(OctoObject obj : uniq_changed)
			for(OctoResponse response : obj.PreparePendingReactions())
			{
				new_responses.add(new PreparedResponse(response, obj, JavaUtils.GetTimestamp()));
			}

		if(!silent)
			pending_response.addAll(new_responses);
		else if(!new_responses.isEmpty())
			System.err.println("silent mode, reactions ignored: " + new_responses.size());
	}
}
