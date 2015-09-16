/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.requests;

/**
 * plain class for storing request result and its type<br>
 * */
public class RequestResult
{
	public enum EResultType
	{
		TEXT,
		JSON,
		JS,
		ERROR
	}

	public final EResultType type;
	public final String data;

	public RequestResult(EResultType type, String data)
	{
		this.type = type;
		this.data = data;
	}
}
