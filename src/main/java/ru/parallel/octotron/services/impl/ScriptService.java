/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.services.impl;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.services.BGExecutorService;
import ru.parallel.octotron.services.BGService;
import ru.parallel.utils.Timer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;

public class ScriptService extends BGService
{
	public ScriptService(Context context)
	{
		super(context, new BGExecutorService("scripts", context.settings.GetNumThreads()
			, DEFAULT_QUEUE_LIMIT));
	}

	public void ExecSilent(final String... command)
		throws ExceptionSystemError
	{
		if(command.length == 0)
			throw new ExceptionSystemError("can not execute empty command");

		executor.execute(
			new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						ProcessBuilder pb = new ProcessBuilder(command);
						pb.redirectErrorStream(true); // merge stdout and stderr of process

						Process process = pb.start();
						Timer.SStart();

						InputStreamReader isr = new InputStreamReader(process.getInputStream());
						BufferedReader br = new BufferedReader(isr);

						String line;
						String output = "";

						while ((line = br.readLine()) != null)
						{
							output += line + System.lineSeparator();
						}

						if(output.length() > 0)
							LOGGER.log(Level.INFO, output);

						process.waitFor();
						LOGGER.log(Level.FINE, command[0] + " finished, took: " + Timer.SGet());

						process.destroy();
						br.close();
						isr.close();
					}
					catch(IOException | InterruptedException e)
					{
						System.out.println("command: " + Arrays.toString(command));
						e.printStackTrace();
					}
				}
			});
	}
}
