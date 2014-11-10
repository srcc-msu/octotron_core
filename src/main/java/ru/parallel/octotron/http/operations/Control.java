/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.FormattedOperation;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.utils.AutoFormat;
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
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params);

			controller.SetExit(true);

			return new TextString("quiting now");
		}
	}

	/**
	 * asks the execution control to perform a self-test<br>
	 * */
	public static class selftest extends FormattedOperation
	{
		public selftest() {super("selftest", true);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params);

			Map<String, Object> result = RuntimeService.PerformSelfTest(controller);

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
		public TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "silent");

			String mode_str = params.get("silent");
			boolean mode = (boolean) SimpleAttribute.ValueFromStr(mode_str);

			controller.SetSilent(mode);

			if(mode)
				return new TextString("silent mode activated - no reactions will be invoked");
			else
				return new TextString("silent mode deactivated");
		}
	}

	/**
	 * collects and shows current model snapshot<br>
	 * */
	public static class snapshot extends FormattedOperation
	{
		public snapshot() {super("snapshot", true);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params);

			List<Map<String, Object>> data = RuntimeService.MakeSnapshot(controller.GetContext().model_data);

			return AutoFormat.PrintJson(data);
		}
	}

	/**
	 * collects and shows statistics<br>
	 * */
	public static class stat extends FormattedOperation
	{
		public stat() {super("stat", true);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			return AutoFormat.PrintJson(controller.GetStat());
		}
	}

	/**
	 * collects and shows attributes, which were modified long ago<br>
	 * param - for how long (in seconds)<br>
	 * */
	public static class mod_time extends FormattedOperation
	{
		public mod_time() {super("mod_time", true);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "interval");

			String value_str = params.get("interval");
			long interval = (long)SimpleAttribute.ValueFromStr(value_str);

			List<Map<String, Object>> result;

			try
			{
				result = RuntimeService.CheckModTime(controller.GetContext(), interval);
			}
			catch (ExceptionModelFail e)
			{
				throw new ExceptionParseError(e);
			}

			return AutoFormat.PrintJson(result);
		}
	}
}
