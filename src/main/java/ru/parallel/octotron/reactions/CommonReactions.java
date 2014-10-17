/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;

public final class CommonReactions
{
	private CommonReactions() {}

	public static Response Critical(String tag, String message)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.CRITICAL)
			.Msg(tag, message)
			.Exec("on_critical");
	}

	public static Response Danger(String tag, String message)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.DANGER)
			.Msg(tag, message)
			.Exec("on_danger");
	}

	public static Response Warning(String tag, String message)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.WARNING)
			.Msg(tag, message)
			.Exec("on_warning");
	}

	public static Response Info(String tag, String message)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.INFO)
			.Msg(tag, message)
			.Exec("on_info");
	}

	public static Response Recover(String tag, String message)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.RECOVER)
			.Msg(tag, message)
			.Exec("on_recover");
	}
}
