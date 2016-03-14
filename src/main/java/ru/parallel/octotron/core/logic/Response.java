/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import com.google.common.collect.ObjectArrays;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.exception.ExceptionModelFail;
import ru.parallel.octotron.exception.ExceptionParseError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * response describes exactly what reaction will be executed:
 * log messages to be generated and scripts to be executed
 * */
public class Response implements IPresentable
{
	private final String status;

	private final Map<String, String> messages;
	private final List<String[]> commands = new LinkedList<>();

	public Response(String status, String... strings)
		throws ExceptionParseError
	{
		this.status = status;

		this.messages = new HashMap<>();

		for(String string : strings)
			Msg(string);
	}

	public String GetStatus()
	{
		return status;
	}

	public Map<String, String> GetMessages()
	{
		return messages;
	}

	public List<String[]> GetCommands()
	{
		return commands;
	}

	public Response Msg(String string)
		throws ExceptionParseError
	{
		Msg(GetHash(string), GetMessage(string));
		return this;
	}

	public Response Msg(String tag, String message)
	{
		if(messages.get(tag) != null)
			throw new ExceptionModelFail("tag already defined: " + tag);
		messages.put(tag, message);
		return this;
	}

	public Response Exec(String script_key, String... arguments)
	{
		commands.add(ObjectArrays.concat(script_key, arguments));
		return this;
	}

	private final char TAG_SEPARATOR = '#';

	private String GetHash(String string)
		throws ExceptionParseError
	{
		int pos = string.indexOf(TAG_SEPARATOR);

		if(pos == -1)
			throw new ExceptionParseError("tag not found: " + string);

		return string.substring(0, pos);
	}

	private String GetMessage(String string)
		throws ExceptionParseError
	{
		int pos = string.indexOf(TAG_SEPARATOR);

		if(pos == -1)
			throw new ExceptionParseError("tag not found: " + string);

		return string.substring(pos + 1);
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("status", status);

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = GetShortRepresentation();

		result.put("messages", messages);
		result.put("commands", commands);

		return result;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}
}
