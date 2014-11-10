/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils.format;

public class JsonpString extends TypedString
{
	public JsonpString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "application/javascript";
	}
}
