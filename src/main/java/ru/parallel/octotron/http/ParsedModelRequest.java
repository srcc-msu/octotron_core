/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.http.Operations.Operation;

import java.util.Map;

/**
 * parsed request with correct tokens list<br>
 * */
public class ParsedModelRequest
{
	final Operation operation;
	final Map<String, String> params;
	private final String query;

	public ParsedModelRequest(Operation operation, Map<String, String> params, String query)
	{
		this.operation = operation;
		this.params = params;
		this.query = query;
	}

	public boolean IsBlocking()
	{
		return operation.IsBlocking();
	}

	public String GetQuery()
	{
		return query;
	}
}
