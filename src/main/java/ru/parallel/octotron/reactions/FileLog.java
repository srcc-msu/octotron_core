/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.utils.JavaUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class FileLog
{
	public static final String FILENAME = "octotron.events.log";

	private BufferedWriter out = null;

	public FileLog(String dir)
		throws ExceptionSystemError
	{
		out = Open(dir + "/" + FILENAME);
	}

	private BufferedWriter Open(String fname)
		throws ExceptionSystemError
	{
		String suffix = JavaUtils.GetDate();

		try
		{
			out = new BufferedWriter(new FileWriter(fname + "." + suffix, true));
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}

		return out;
	}

	public void Log(String str)
		throws ExceptionSystemError
	{
		if(out == null)
			throw new ExceptionSystemError("files has been closed already");

		try
		{
			out.write(str + System.lineSeparator());
			out.flush();
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	public void Close()
		throws ExceptionSystemError
	{
		try
		{
			if(out != null)
				out.close();

			out = null;
		}
		catch (IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}
}
