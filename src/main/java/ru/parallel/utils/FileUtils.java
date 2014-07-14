/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FileUtils
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	public static BufferedReader InitStream(String[] command)
		throws ExceptionSystemError
	{
		Process process;

		try
		{
			process = new ProcessBuilder(command).start();
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
//		InputStream er = process.getErrorStream();

		return new BufferedReader(isr);
	}

	private static final Queue<Process> active_processes = new ConcurrentLinkedQueue<>();
	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				int counter = 0;

				for(Process p : FileUtils.active_processes)
				{
					p.destroy();
					counter++;
				}

				if(counter > 0)
					LOGGER.log(Level.WARNING, counter
						+ " processes did not finish and were killed");
			}
		});
	}

	public static void ExecSilent(String... command)
		throws ExceptionSystemError
	{
		FileUtils.ExecSilent(false, command);
	}

	public static void ExecSilent(boolean blocking, final String... command)
		throws ExceptionSystemError
	{
		if(command.length == 0)
			throw new ExceptionSystemError("can not execute empty command");

		Thread exec_thread = new Thread()
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
					FileUtils.active_processes.add(process);

					InputStreamReader isr = new InputStreamReader(
						process.getInputStream());
					BufferedReader br = new BufferedReader(isr);

					String line;
					String output = "";

					while ((line = br.readLine()) != null)
					{
						output += line + System.lineSeparator();
					}

					LOGGER.log(Level.INFO, output);

					process.waitFor();
LOGGER.log(Level.INFO, command[0] + " finished, took: " + Timer.SGet());

					process.destroy();
					br.close();
					isr.close();

					FileUtils.active_processes.remove(process);
				}
				catch(IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};

		exec_thread.setName("exec thread");
		exec_thread.start();

		if(blocking)
		{
			try
			{
				exec_thread.join();
			}
			catch (InterruptedException e)
			{
				throw new ExceptionSystemError(e);
			}
		}
	}

	public static String FileToString(String fname)
		throws ExceptionSystemError
	{
		try
		{
			FileReader fr;
			fr = new FileReader(fname);
			BufferedReader reader = new BufferedReader(fr);

			StringBuilder text = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null)
				text.append(line);

			reader.close();

			return text.toString();
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	public static String[] FileToStringArr(String fname)
		throws ExceptionSystemError
	{
		try
		{
			FileReader fr = new FileReader(fname);
			BufferedReader reader = new BufferedReader(fr);

			List<String> text = new LinkedList<>();
			String line;

			while ((line = reader.readLine()) != null)
				text.add(line);

			reader.close();

			String[] res = new String[text.size()];
			text.toArray(res);

			return res;
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	public static boolean IsDirEmpty(String name)
		throws ExceptionSystemError
	{
		File file = new File(name);

		if(!file.exists())
			return true;

		if(!file.isDirectory() || file.listFiles() == null)
			throw new ExceptionSystemError("not a directory: " + name);

		return file.listFiles().length == 0;
	}

	public static void WipeDir(String name)
		throws ExceptionSystemError
	{
		WipeDir(new File(name));
	}

	/**
	 * Service method for deleting a directory<br>
	 */
	public static void WipeDir(File file)
		throws ExceptionSystemError
	{
		if(!file.exists())
			return;

		if(file.isDirectory() && file.listFiles() != null)
		{
			for(File child : file.listFiles())
				FileUtils.WipeDir(child);
		}

		if(!file.delete())
			throw new ExceptionSystemError("can not delete: " + file.getAbsolutePath());
	}

	public static void SaveToFile(String fname, String string)
		throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(fname);
		writer.write(string);
		writer.close();
	}
}
