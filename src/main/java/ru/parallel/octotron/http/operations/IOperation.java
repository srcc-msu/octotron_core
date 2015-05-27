/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public abstract interface IOperation
{
	String GetName();
	boolean IsBlocking();

	public abstract TypedString Execute(Map<String, String> params) throws ExceptionParseError;
}
