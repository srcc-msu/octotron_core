/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.primitive.exception;

/**
 * thrown when something wrong with database-level operations<br>
 * */
@SuppressWarnings("serial")
public class ExceptionDBError extends RuntimeException
{
	public ExceptionDBError(String err)
	{
		super(err);
	}
	public ExceptionDBError(Exception wrap)
	{
		super(wrap);
	}
}

