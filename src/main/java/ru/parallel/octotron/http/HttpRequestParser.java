/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.http.Operations.Operation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class for parsing http-compatible query<br>
 * */
public class HttpRequestParser
{
	private HttpRequestParser(){}

	/**
	 * tokens that are allowed in the view request<br>
	 * this requests are used to retrieve current model state<br>
	 * */
	private static final Operation[] VIEW_OPERATIONS =
	{
		Operations.version
		, Operations.count
		, Operations.p
		, Operations.p_react
		, Operations.p_const, Operations.p_sensor, Operations.p_var
		, Operations.show_m, Operations.show_r
		, Operations.export
	};

	/**
	 * tokens that are allowed in the modify requests<br>
	 * this requests are used for import and manual model manipulations<br>
	 * */
	private static final Operation[] MODIFY_OPERATIONS =
	{
		Operations.import_token, Operations.unchecked_import_token
		, Operations.set, Operations.static_op
		, Operations.set_valid, Operations.set_invalid
		, Operations.add_m, Operations.del_m
	};

	/**
	 * tokens that are allowed in the control request<br>
	 * this requests are used for database administration operations<br>
	 * */
	private static final Operation[] CONTROL_OPERATIONS =
	{
		Operations.quit, Operations.mode
		, Operations.snapshot, Operations.selftest
		, Operations.stat, Operations.mod_time
	};

	/**
	 * all available request types<br>
	 * */
	private static final Map<String, Operation[]> REQUEST_TYPES = new HashMap<>();
	static
	{
		HttpRequestParser.REQUEST_TYPES.put("view", HttpRequestParser.VIEW_OPERATIONS);
		HttpRequestParser.REQUEST_TYPES.put("modify", HttpRequestParser.MODIFY_OPERATIONS);
		HttpRequestParser.REQUEST_TYPES.put("control", HttpRequestParser.CONTROL_OPERATIONS);
	}

	/**
	 * parse an http-compatible params string into map of pairs<br>
	 * if the value is empty, null is stored in value<br>
	 * string example: param1=value&param2&param3=value2<br>
	 * */
	private static Map<String, String> ParseParams(String query) {
		Map<String, String> result = new HashMap<>();

		if(query == null || query.isEmpty())
			return result;

		String[] operations = query.split("&");

		for(String operation : operations)
		{
			int idx = operation.indexOf("=");

			String name;
			String params;

			if (idx != -1)
			{
				name = operation.substring(0, idx);
				params = operation.substring(idx + 1, operation.length());
			}
			else
			{
				name = operation;
				params = null;
			}

			result.put(name, params);
		}

		return result;
	}

	/**
	 * check that specified request type exists<br>
	 * check the operation is allowed here<br>
	 * parse params string and return ParsedRequest<br>
	 * */
	private static ParsedModelRequest ParseModelRequest(String request_type, String operation_name, String query)
		throws ExceptionParseError
	{
		try
		{
			Operation[] operations = HttpRequestParser.REQUEST_TYPES.get(request_type);

			if(operations == null)
				throw new ExceptionParseError("wrong request type: " + request_type);

			Operation operation = null;

			for(Operation op : operations)
				if(op.GetName().equals(operation_name))
				{
					operation = op;
					break;
				}

			if(operation == null)
				throw new ExceptionParseError("unknown operation: " + operation_name);

			Map<String, String> params = HttpRequestParser.ParseParams(query);

			return new ParsedModelRequest(operation, params);
		}
		catch(ExceptionParseError e)
		{
			String res = "could not parse request: " + e + System.lineSeparator();

			for(StackTraceElement s : e.getStackTrace())
				res += System.lineSeparator() + s;

			throw new ExceptionParseError(res);
		}
	}

	private static final Pattern pattern = Pattern.compile("^/([a-zA-Z_]+)/([a-zA-Z_]+)$");

	/**
	 * parse http request into a ParsedHttp object, filling all required information<br>
	 * */
	public static ParsedModelRequest ParseFromExchange(HttpExchangeWrapper http_request)
		throws ExceptionParseError
	{
		Matcher matcher = pattern.matcher(http_request.GetPath());

		if(matcher.find())
		{
			String request_type = matcher.group(1);
			String operation_name = matcher.group(2);

			ParsedModelRequest parsed_model_request = HttpRequestParser.ParseModelRequest(
				request_type, operation_name, http_request.GetQuery());

			return parsed_model_request;
		}
		else
			throw new ExceptionParseError("URI is not in format /request_type/operation");
	}
}
