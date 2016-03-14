/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils;

import ru.parallel.octotron.exception.ExceptionSystemError;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

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
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
//		InputStream er = process.getErrorStream();

		return new BufferedReader(isr);
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

		File[] files = file.listFiles();

		if(files == null)
			throw new ExceptionSystemError("unknown error, could not list files in directory: " + name);

		return files.length == 0;
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
			File[] files = file.listFiles();

			if(files == null)
				throw new ExceptionSystemError("unknown error, could not list files in directory: " + file.getAbsolutePath());

			for(File child : files)
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
