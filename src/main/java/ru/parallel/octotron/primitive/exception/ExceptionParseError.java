/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.primitive.exception;

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
