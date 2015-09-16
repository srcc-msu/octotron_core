/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive.exception;

/**
 * thrown when something is wrong with objects:<br>
 * mismatch types, missing attributes, etc.<br>
 * */
@SuppressWarnings("serial")
public class ExceptionModelFail extends RuntimeException
{
	public ExceptionModelFail(String message)
	{
		super(message);
	}
	public ExceptionModelFail(Exception wrap)
{
	super(wrap);
}
}
