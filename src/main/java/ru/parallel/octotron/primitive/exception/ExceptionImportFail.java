/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.primitive.exception;

/**
 * thrown when import failed by some reason.<br>
 * */
@SuppressWarnings("serial")
public class ExceptionImportFail extends Exception
{
	public ExceptionImportFail(String message)
	{
		super(message);
	}
}
