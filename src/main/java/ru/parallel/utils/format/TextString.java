/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils.format;

public class TextString extends TypedString
{
	public TextString(String string)
	{
		super(string);
	}

	@Override
	public String GetContentType()
	{
		return "text/plain";
	}
}
