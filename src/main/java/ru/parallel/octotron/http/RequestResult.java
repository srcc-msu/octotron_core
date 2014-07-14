/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

/**
 * plain class for storing two request result and its type<br>
 * */
public class RequestResult
{
	public enum E_RESULT_TYPE
	{
		TEXT,
		CSV,
		JSON,
		JSONP,
		ERROR
	}

	public final E_RESULT_TYPE type;
	public final String data;

	public RequestResult(E_RESULT_TYPE type, String data)
	{
		this.type = type;
		this.data = data;
	}
}
