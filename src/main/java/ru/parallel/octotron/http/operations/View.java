/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.attributes.impl.Reaction;
import ru.parallel.octotron.core.attributes.impl.Sensor;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.http.operations.impl.FormattedOperation;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.octotron.services.ServiceLocator;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.JavaUtils;
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
		public TypedString Execute(Map<String, String> params, boolean verbose
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			return new TextString(String.valueOf(entities.size()));
		}
	}

	/**
	 * prints attributes of the given entity list<br>
	 * attribute names and format are taken from params<br>
	 * */
	public static class attributes extends ModelOperation
	{
		public attributes()
		{
			super("attributes", true);
		}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "names");
			Utils.AllParams(params, "names", "format");

			List<String> attributes = Utils.GetNames(params.get("names"));
			String format = params.get("format");

			if(format == null || format.equals(")json") || format.equals("jsonp"))
				return AutoFormat.PrintJson(Utils.GetAttributes(entities, attributes, verbose));
			if(format.equals("csv"))
				return new TextString(Utils.PrintCsvAttributes(entities, attributes)); // TODO: it should be CsvString her, but it makes debugging harder

			else return new ErrorString("unsupported format: " + format);
		}
	}

	/**
	 * prints attributes of the given entity list<br>
	 * attribute names and format are taken from params<br>
	 * */
	public static class import_map extends ModelOperation
	{
		public import_map()
		{
			super("import_map", true);
		}

		public String MakeCsvImportMap(ModelList<? extends ModelEntity, ?> entities
			, List<String> attributes)
		{
			StringBuilder result = new StringBuilder();
			String sep = ",";

			String prefix = "";

			result.append("name,timeout,");
			for(String name : attributes)
			{
				result.append(prefix).append(name);
				prefix = sep;
			}

			result.append(System.lineSeparator());

			for(ModelEntity entity : entities)
			{
				StringBuilder add_attributes = new StringBuilder();
				prefix = "";
				for(String name : attributes)
				{
					String string_value;

					if(entity.TestAttribute(name))
						string_value = entity.GetAttribute(name).ValueToString();
					else
						string_value = "<not found>";

					add_attributes.append(prefix).append(string_value);
					prefix = sep;
				}
				add_attributes.append(System.lineSeparator());

				for(Sensor sensor : entity.GetSensor())
				{
					result.append(String.format("%s,%d,"
						, JavaUtils.Quotify(sensor.GetName())
						, sensor.GetUpdateInterval()));

					result.append(add_attributes.toString());
				}
			}

			return result.toString();
		}

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.AllParams(params, "names");

			List<String> attributes = Utils.GetNames(params.get("names"));

			String result = MakeCsvImportMap(entities, attributes);

			return new TextString(result);
		}
	}

	public static class entity extends ModelOperation
	{
		public entity()
		{
			super("entity", true);
		}

		public static final List<String> allowed_types = Arrays.asList("const", "sensor", "var", "trigger", "reaction");

		@Override
		public TypedString Execute(Map<String, String> params, boolean verbose
			, ModelList<? extends ModelEntity, ?> entities)
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
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			List<Map<String, Object>> result = new LinkedList<>();

			for(Reaction reaction : ServiceLocator.INSTANCE.GetModelService().GetSuppressedReactions())
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
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.AllParams(params);

			List<Map<String, Object>> result = new LinkedList<>();

			for(ModelEntity entity : ServiceLocator.INSTANCE.GetModelService().GetModelData().GetAllEntities())
				for(Reaction reaction : entity.GetReaction())
					result.add(reaction.GetTemplate().response.GetShortRepresentation());

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
		public TypedString Execute(Map<String, String> params, boolean verbose)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params);
			Utils.AllParams(params);

			try
			{
				return AutoFormat.PrintJson(ServiceLocator.INSTANCE.GetRuntimeService().GetVersion());
			}
			catch(ExceptionSystemError e)
			{
				return new ErrorString("could not read Version: " + e.getMessage());
			}
		}
	}
}
