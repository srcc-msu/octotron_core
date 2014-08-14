/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.OctoResponse;

public final class CommonReactions
{
	private CommonReactions() {}

	public static OctoResponse Critical(String str, String... attributes)
	{
		return new OctoResponse(EEventStatus.CRITICAL, str)
			.Print("AID")
			.Print(attributes)
			.Log("errors")
			.Log("all")
			.Exec("on_critical");
	}

	public static OctoResponse Danger(String str, String... attributes)
	{
		return new OctoResponse(EEventStatus.DANGER, str)
			.Print("AID")
			.Print(attributes)
			.Log("errors")
			.Log("all")
			.Exec("on_danger");
	}

	public static OctoResponse Warning(String str, String... attributes)
	{
		return new OctoResponse(EEventStatus.WARNING, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_warning");
	}

	public static OctoResponse Info(String str, String... attributes)
	{
		return new OctoResponse(EEventStatus.INFO, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_info");
	}

	public static OctoResponse Recover(String str, String... attributes)
	{
		return new OctoResponse(EEventStatus.RECOVER, str)
			.Print("AID")
			.Print(attributes)
			.Log("all")
			.Exec("on_recover");
	}
}
