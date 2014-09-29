/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.graph.collections.AutoFormat;

import static ru.parallel.octotron.core.graph.collections.AutoFormat.E_FORMAT_PARAM;

/**
 * plain class for storing two request result and its type<br>
 * */
public class RequestResult
{
	public enum E_RESULT_TYPE
	{
		TEXT,
		JSON,
		ERROR
	}

	public final E_RESULT_TYPE type;
	public final String data;

	public RequestResult(E_RESULT_TYPE type, String data)
	{
		this.type = type;
		this.data = data;
	}

	public RequestResult(E_FORMAT_PARAM format, String data)
	{
		switch(format)
		{
			case PLAIN: this.type = E_RESULT_TYPE.TEXT; break;
			case JSON : this.type = E_RESULT_TYPE.JSON; break;
			case JSONP: this.type = E_RESULT_TYPE.JSON; break;
			default   : this.type = E_RESULT_TYPE.TEXT; break;
		}

		this.data = data;
	}
}
