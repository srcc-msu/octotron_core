/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.utils.JavaUtils;


public class FileLog
{
	private BufferedWriter out = null;

	public FileLog(String fname)
		throws ExceptionSystemError
	{
		out = Open(fname);
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
