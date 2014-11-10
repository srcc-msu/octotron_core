/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.FormattedOperation;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.octotron.reactions.CommonReactions;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * implementation of all available http operations<br>
 * */
public abstract class View
{
	/**
	 * print size of the given list<br>
	 * */
	public static class count extends ModelOperation
	{
		public count()
		{
			super("count", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			return new TextString(String.valueOf(entities.size()));
		}
	}

	/**
	 * prints properties of the given list entities<br>
	 * properties and format are taken from params<br>
	 * */
	public static class attribute extends ModelOperation
	{
		public attribute()
		{
			super("attribute", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "names");
			Utils.AllParams(params, "names", "format");

			List<SimpleAttribute> attributes = Utils.GetAttributes(params.get("names"));
			String format = params.get("format");

			if(format == null || format.equals("json") || format.equals("jsonp"))
				return AutoFormat.PrintJson(Utils.GetAttributes(entities, attributes, verbose));
			if(format.equals("csv"))
				return new TextString(Utils.PrintCsvAttributes(entities, attributes));

			else return new ErrorString("unsupported format: " + format);
		}
	}

	public static class entity extends ModelOperation
	{
		public entity()
		{
			super("entity", true);
		}

		public static List<String> allowed_types = Arrays.asList("const", "var", "sensor");

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.AllParams(params, "type");
			String type = params.get("type");

			if(type != null)
			{
				if(allowed_types.contains(type))
					return AutoFormat.PrintJson(Utils.GetAttributes(entities, verbose, type));
				else
					return new ErrorString("unknown type: " + type);
			}
			else
				return AutoFormat.PrintJson(Utils.GetAttributes(entities, verbose));
		}
	}

	public static class reaction extends ModelOperation
	{
		public reaction()
		{
			super("reaction", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");
			String name = params.get("name");

			List<Map<String, Object>> data = new LinkedList<>();

			for(Reaction reaction : entities.Only().GetAttribute(name).GetReactions())
			{
				data.add(reaction.GetRepresentation(verbose));
			}

			return AutoFormat.PrintJson(data);
		}
	}

	/**
	 * show all markers in model<br>
	 * */
	public static class suppressed extends FormattedOperation
	{

		public suppressed()
		{
			super("suppressed",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			List<Map<String, Object>> result = new LinkedList<>();

			for(Reaction reaction : controller.GetContext().model_service.GetSuppressedReactions())
			{
				result.add(reaction.GetRepresentation(verbose));
			}

			return AutoFormat.PrintJson(result);
		}
	}

	/**
	 * show all reactions in model<br>
	 * */
	public static class all_response extends FormattedOperation
	{

		public all_response()
		{
			super("all_response",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			List<Response> responses = CommonReactions.GetRegisteredResponses();

			List<Map<String, Object>> result = new LinkedList<>();

			for(Response response : responses)
				result.add(response.GetShortRepresentation());

			return AutoFormat.PrintJson(result);
		}
	}

	/**
	 * collects and shows statistics<br>
	 * */
	public static class version extends FormattedOperation
	{

		public version()
		{
			super("version",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params);
			Utils.AllParams(params);

			try
			{
				return AutoFormat.PrintJson( RuntimeService.GetVersion());
			}
			catch(ExceptionSystemError e)
			{
				return new ErrorString("could not read Version: " + e.getMessage());
			}
		}
	}
}
