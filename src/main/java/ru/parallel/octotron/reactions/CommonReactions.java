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

	public static Response Critical(String str, String... attributes)
	{
		return new Response(EEventStatus.CRITICAL, str)
			.Print("AID")
			.Print(attributes)
			.Log("errors")
			.Log("all")
			.Exec("on_critical");
	}

	public static Response Danger(String str, String... attributes)
	{
		return new Response(EEventStatus.DANGER, str)
			.Print("AID")
			.Print(attributes)
			.Log("errors")
			.Log("all")
			.Exec("on_danger");
	}

	public static Response Warning(String str, String... attributes)
	{
		return new Response(EEventStatus.WARNING, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_warning");
	}

	public static Response Info(String str, String... attributes)
	{
		return new Response(EEventStatus.INFO, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_info");
	}

	public static Response Recover(String str, String... attributes)
	{
		return new Response(EEventStatus.RECOVER, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_recover");
	}
}
