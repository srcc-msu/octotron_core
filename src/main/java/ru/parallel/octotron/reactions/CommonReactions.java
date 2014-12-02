/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

import java.util.LinkedList;
import java.util.List;

public final class CommonReactions
{
	private CommonReactions() {}

	private static final List<Response> registered_responses = new LinkedList<>();

	public static Response Critical(String tag, String message)
		throws ExceptionParseError
	{
		Response response = new Response(EEventStatus.CRITICAL).Msg(tag, message).Exec("on_critical");

		RegisterResponse(response);
		return response;
	}

	private static void RegisterResponse(Response response)
	{
		registered_responses.add(response);
	}

	public static List<Response> GetRegisteredResponses()
	{
		return registered_responses;
	}

	public static Response Danger(String tag, String message)
		throws ExceptionParseError
	{
		Response response = new Response(EEventStatus.DANGER).Msg(tag, message).Exec("on_danger");

		RegisterResponse(response);
		return response;
	}

	public static Response Warning(String tag, String message)
		throws ExceptionParseError
	{
		Response response = new Response(EEventStatus.WARNING).Msg(tag, message).Exec("on_warning");

		RegisterResponse(response);
		return response;
	}

	public static Response Info(String tag, String message)
		throws ExceptionParseError
	{
		Response response = new Response(EEventStatus.INFO).Msg(tag, message).Exec("on_info");

		RegisterResponse(response);
		return response;
	}

	public static Response Recover(String tag, String message)
		throws ExceptionParseError
	{
		Response response = new Response(EEventStatus.RECOVER).Msg(tag, message).Exec("on_recover");

		RegisterResponse(response);
		return response;
	}
}
