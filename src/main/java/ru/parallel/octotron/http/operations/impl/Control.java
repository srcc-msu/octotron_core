/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations.impl;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.exception.ExceptionParseError;
import ru.parallel.octotron.bg_services.ServiceLocator;
import ru.parallel.octotron.http.operations.FormattedOperation;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.JavaUtils;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Control
{
	/**
	 * asks the execution control to stop the main process<br>
	 * */
	public static class quit extends FormattedOperation
	{
		public quit() {super("quit", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params);

			ServiceLocator.INSTANCE.GetRuntimeService().Terminate(true);

			return new TextString("quiting now");
		}
	}

	/**
	 * asks the execution control to stop the main process<br>
	 * */
	public static class check extends FormattedOperation
	{
		public check() {super("check", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params, "time");

			String time_str = params.get("time");

			long time = -1;

			if(time_str != null)
				time = JavaUtils.GetTimestamp();

			boolean result = ServiceLocator.INSTANCE.GetOutdatedCheckerService().PerformCheck(time);

			if(result)
				return new TextString("running outdated check");
			return new TextString("outdated check is already running");
		}
	}

	/**
	 * asks the execution control to perform a self-test<br>
	 * */
	public static class selftest extends FormattedOperation
	{
		public selftest() {super("selftest", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params);

			Map<String, Object> result = ServiceLocator.INSTANCE.GetRuntimeService().PerformSelfTest();

			return AutoFormat.PrintJson(Collections.singleton(result));
		}
	}

	/**
	 * asks the execution control to change mode<br>
	 * currently there is only one mode - silent<br>
	 * no reactions will be invoked in silent mode<br>
	 * */
	public static class mode extends FormattedOperation
	{
		public mode() {super("mode", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params, "silent");

			String mode_str = params.get("silent");
			if(mode_str != null)
			{
				boolean mode = Value.ValueFromString(mode_str).GetBoolean();

				ServiceLocator.INSTANCE.GetReactionService().SetSilent(mode);
			}

			boolean is_silent = ServiceLocator.INSTANCE.GetReactionService().IsSilent();

			return new TextString("silent mode: " + Boolean.toString(is_silent));
		}
	}

	/**
	 * collects and shows current model snapshot<br>
	 * */
	public static class snapshot extends FormattedOperation
	{
		public snapshot() {super("snapshot", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params, "format");
			String format = params.get("format");

			if(format == null || format.equals("json") || format.equals("jsonp"))
			{
				return AutoFormat.PrintJson(ServiceLocator.INSTANCE.GetRuntimeService().MakeSnapshot(verbose));
			}
			else if(format.equals("csv"))
				return new TextString(ServiceLocator.INSTANCE.GetRuntimeService().MakeCsvSnapshot());
			else
				return new ErrorString("unsupported format: " + format);
		}
	}

	/**
	 * collects and shows statistics<br>
	 * */
	public static class stat extends FormattedOperation
	{
		public stat() {super("stat", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			return AutoFormat.PrintJson(ServiceLocator.INSTANCE.GetRuntimeService().GetStat().GetRepresentation());
		}
	}

	/**
	 * collects and shows attributes, which were modified long ago<br>
	 * param - for how long (in seconds)<br>
	 * */
	public static class timeout extends FormattedOperation
	{
		public timeout() {super("timeout", true);}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			List<Map<String, Object>> result
				= ServiceLocator.INSTANCE.GetRuntimeService().CheckTimeout();

			return AutoFormat.PrintJson(result);
		}
	}
}
