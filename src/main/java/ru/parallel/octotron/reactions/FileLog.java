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

// TODO rework
public class FileLog
{
	private final String path;
	private BufferedWriter out;

	public FileLog(String dir, String fname)
		throws ExceptionSystemError
	{
		this.path = dir + "/" + fname;
		this.out = Open();
	}

	private BufferedWriter Open()
		throws ExceptionSystemError
	{
		String suffix = JavaUtils.GetDate();

		try
		{
			return new BufferedWriter(new FileWriter(path + "." + suffix, true));
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	private void NotClosed()
		throws ExceptionSystemError
	{
		if(out == null)
			throw new ExceptionSystemError("files has been closed already");
	}

	public void Log(String str)
		throws ExceptionSystemError
	{
		NotClosed();
		try
		{
			out.write(str + System.lineSeparator());
			out.flush();
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}

	public void Close()
		throws ExceptionSystemError
	{
		NotClosed();

		try
		{
			out.close();
			out = null;
		}
		catch(IOException e)
		{
			throw new ExceptionSystemError(e);
		}
	}
}
