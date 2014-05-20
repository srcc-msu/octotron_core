/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

public class ParsedHttpRequest
{
	private final ParsedRequest parsed_request;
	private final HTTPRequest http_request;

	public ParsedHttpRequest(HTTPRequest http_request, ParsedRequest parsed_request)
	{
		this.http_request = http_request;
		this.parsed_request = parsed_request;
	}

	public ParsedRequest GetParsedRequest()
	{
		return parsed_request;
	}

	public HTTPRequest GetHttpRequest()
	{
		return http_request;
	}
}
