/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exception;

/**
 * thrown when something went wrong on the system level<br>
 **/
@SuppressWarnings("serial")
public class ExceptionSystemError extends Exception
{
	public ExceptionSystemError(String message)
	{
		super(message);
	}

	public ExceptionSystemError(Exception wrap)
	{
		super(wrap);
	}
}
