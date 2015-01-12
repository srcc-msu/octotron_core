/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public class Modify
{
	/**
	 * adds a single import value to import queue<br>
	 * */
	public static class import_op extends ModelOperation
	{
		public import_op() {super("import", false);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Value value = Value.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			controller.update_service.Import(target, name, value);

			return new TextString("added to import queue");
		}
	}

	/**
	 * adds a single import value to unchecked import queue<br>
	 * */
	public static class unchecked_import extends ModelOperation
	{
		public unchecked_import() {super("unchecked_import", false);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Value value = Value.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			try
			{
				if(controller.update_service.Import(target, name, value, false))
					return new TextString("added to import queue");
				else
					return new TextString("attribute not found, but registered, import skipped");
			}
			catch(ExceptionSystemError e)
			{
				throw new ExceptionModelFail(e);
			}
		}
	}

	/**
	 * set invalid state to all given entities<br>
	 * */
	public static class set_valid extends ModelOperation
	{
		public set_valid() {super("set_valid", true);}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");

			String name = params.get("name");

			for(ModelEntity entity : entities)
			{
				SensorAttribute sensor = entity.GetSensor(name);
				sensor.SetUserValid();
				controller.update_service.Update(sensor, true);
			}

			return new TextString("set the attribute to valid for " + entities.size() + " entities");
		}
	}

	/**
	 * set invalid state to all given entities<br>
	 * */
	public static class set_invalid extends ModelOperation
	{
		public set_invalid() {super("set_invalid", true);}


		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.StrictParams(params, "name");

			String name = params.get("name");

			for(ModelEntity entity : entities)
			{
				SensorAttribute sensor = entity.GetSensor(name);
				sensor.SetUserInvalid();
				controller.update_service.Update(sensor, true);
			}

			return new TextString("set the attribute to invalid for " + entities.size() + " entities");
		}
	}

	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static class suppress extends ModelOperation
	{
		public suppress() {super("suppress", true);}


		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "template_id");
			Utils.AllParams(params, "template_id", "description");

			String template_id_str = params.get("template_id");
			long template_id = Value.ValueFromStr(template_id_str).GetLong();

			String description = params.get("description");
			if(description == null)
				description = "";

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = controller.model_service
					.SetSuppress(entity, template_id, true, description);

				if(AID != -1)
				{
					res += "suppressed reaction: " + AID
						+ " with template: " + template_id
						+ System.lineSeparator();
				}
				else
				{
					res += "reaction with template: " + template_id
						+ " not found on object: " + entity.GetID()
						+ System.lineSeparator();
				}
			}

			return new TextString(res);
		}
	}


	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static class unsuppress extends ModelOperation
	{
		public unsuppress() {super("unsuppress", true);}


		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params
			, boolean verbose, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Utils.RequiredParams(params, "template_id");
			Utils.AllParams(params, "template_id");

			String template_id_str = params.get("template_id");

			long template_id = Value.ValueFromStr(template_id_str).GetLong();

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = controller.model_service
					.SetSuppress(entity, template_id, false, "");

				if(AID != -1)
				{
					res += "unsuppressed reaction: " + AID
						+ " with template: " + template_id
						+ System.lineSeparator();
				}
				else
				{
					res += "reaction with template: " + template_id
						+ " not found on object: " + entity.GetID()
						+ System.lineSeparator();
				}
			}

			return new TextString(res);
		}
	}
}
