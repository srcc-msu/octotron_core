/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils.format;

public class ErrorString extends TypedString
{
	public ErrorString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "text/plain";
	}
}
