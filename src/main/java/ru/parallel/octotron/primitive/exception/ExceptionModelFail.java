/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.primitive.exception;

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
}
