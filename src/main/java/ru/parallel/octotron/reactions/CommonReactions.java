/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.primitive.EEventStatus;

public final class CommonReactions
{
	private CommonReactions() {}

	public static Response Critical(String... messages)
	{
		return new Response(EEventStatus.CRITICAL, messages)
			.Print("AID", "type", "ip")
			.Exec("on_critical");
	}

	public static Response Danger(String... messages)
	{
		return new Response(EEventStatus.DANGER, messages)
			.Print("AID", "type", "ip")
			.Exec("on_danger");
	}

	public static Response Warning(String... messages)
	{
		return new Response(EEventStatus.WARNING, messages)
			.Print("AID", "type", "ip")
			.Exec("on_warning");
	}

	public static Response Info(String... messages)
	{
		return new Response(EEventStatus.INFO, messages)
			.Print("AID", "type", "ip")
			.Exec("on_info");
	}

	public static Response Recover(String... messages)
	{
		return new Response(EEventStatus.RECOVER, messages)
			.Print("AID", "type", "ip")
			.Exec("on_recover");
	}
}
