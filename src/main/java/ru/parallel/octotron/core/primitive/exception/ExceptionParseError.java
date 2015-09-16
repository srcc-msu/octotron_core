/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive.exception;

@SuppressWarnings("serial")
public class ExceptionParseError extends Exception
{
	public ExceptionParseError(String message)
	{
		super(message);
	}

	public ExceptionParseError(Exception e)
	{
		super(e);
	}
}
