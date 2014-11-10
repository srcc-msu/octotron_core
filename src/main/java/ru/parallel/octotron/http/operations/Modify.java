/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.utils.format.ErrorString;
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

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			if(target.GetSensor(name) == null)
				return new ErrorString("sensor does not exist: " + name);

			controller.Import(target, new SimpleAttribute(name, value));

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

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			if(target.GetSensor(name) == null)
			{
				try
				{
					controller.UnknownImport(target, new SimpleAttribute(name, value));
				}
				catch (ExceptionSystemError e)
				{
					throw new ExceptionModelFail(e);
				}

				return new TextString("attribute not found, but registered, import skipped");
			}

			controller.Import(target, new SimpleAttribute(name, value));
			return new TextString("added to unchecked import queue");
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
				entity.GetAttribute(name).SetValid();

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
				entity.GetAttribute(name).SetInvalid();

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
			long template_id = (long)SimpleAttribute.ValueFromStr(template_id_str);

			String description = params.get("description");
			if(description == null)
				description = "";

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = controller.GetContext().model_service
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

			long template_id = (long)SimpleAttribute.ValueFromStr(template_id_str);

			String res = "";

			for(ModelEntity entity : entities)
			{
				long AID = controller.GetContext().model_service
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
