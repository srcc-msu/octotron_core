/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public abstract class FileUtils
{
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
		BufferedReader br = new BufferedReader(isr);

//		InputStream er = process.getErrorStream();

		return br;
	}

	private static Queue<Process> active_processes = new ConcurrentLinkedQueue<Process>();
	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				int counter = 0;

				for(Process p : active_processes)
				{
					p.destroy();
					counter++;
				}

				if(counter > 0)
					System.err.println(counter
						+ " processes did not finish and were killed");
			}
		});
	}

	public static void ExecSilent(final String... command)
		throws ExceptionSystemError
	{
		ExecSilent(false, command);
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

					final Process process = pb.start();
Timer.SStart();
					active_processes.add(process);

					InputStreamReader isr = new InputStreamReader(
						process.getInputStream());
					BufferedReader br = new BufferedReader(isr);

					String line;

					while ((line = br.readLine()) != null)
					{
						System.err.println(line);
					}

					process.waitFor();
Timer.SPrint(command[0]);

					process.destroy();
					br.close();
					isr.close();

					active_processes.remove(process);
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
		catch (IOException e)
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

			List<String> text = new LinkedList<String>();
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

	/**
	 * Service method for deleting all db files<br>
	 */
	public static void WipeDir(File file)
		throws ExceptionSystemError
	{
		if(file.isDirectory())
		{
			for(File child : file.listFiles())
				WipeDir(child);
		}

		if(!file.delete())
			throw new ExceptionSystemError("Can't delete DB!");
	}

	public static void SaveToFile(String fname, String string)
		throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(fname);
		writer.write(string);
		writer.close();
	}
}
