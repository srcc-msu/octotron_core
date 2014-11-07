/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.utils.format;

public abstract class TypedString
{
	public final String string;
	public TypedString(String string)
	{
		this.string = string;
	}
	public abstract String GetContentType();
}
