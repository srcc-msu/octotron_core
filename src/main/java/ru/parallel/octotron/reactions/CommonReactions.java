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

	public static Response Critical(String... messages)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.CRITICAL, messages)
			.Exec("on_critical");
	}

	public static Response Danger(String... messages)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.DANGER, messages)
			.Exec("on_danger");
	}

	public static Response Warning(String... messages)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.WARNING, messages)
			.Exec("on_warning");
	}

	public static Response Info(String... messages)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.INFO, messages)
			.Exec("on_info");
	}

	public static Response Recover(String... messages)
		throws ExceptionParseError
	{
		return new Response(EEventStatus.RECOVER, messages)
			.Exec("on_recover");
	}
}
