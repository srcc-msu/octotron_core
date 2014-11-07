package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.octotron.http.requests.RequestResult;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.Map;

public class Modify
{
	/**
	 * adds a single import value to import queue<br>
	 * */
	public static class import_token extends ModelOperation
	{
		public import_token() {super("import", false);}

		@Override
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			if(target.GetSensor(name) == null)
				return new ErrorString("sesnor does not exist: " + name);

			controller.Import(target, new SimpleAttribute(name, value));

			return new TextString("added to import queue");
		}
	}

	/**
	 * adds a single import value to unchecked import queue<br>
	 * */
	public static class unchecked_import_token extends ModelOperation
	{
		public unchecked_import_token() {super("unchecked_import", false);}

		@Override
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "value");

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
			else
			{
				controller.Import(target, new SimpleAttribute(name, value));
			}

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
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name");

			String name = params.get("name");

			for(ModelEntity entity : entities)
				entity.GetAttribute(name).SetValid();

			int count = entities.size();
			String data = "";

			if(count == 1)
				data = "set valid attribute for one object";
			else if(count > 1)
				data = "set valid attribute for each of " + count + " entities";

			return new TextString(data);
		}
	}

	/**
	 * set invalid state to all given entities<br>
	 * */
	public static class set_invalid extends ModelOperation
	{
		public set_invalid() {super("set_invalid", true);}


		@Override
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name");

			String name = params.get("name");

			for(ModelEntity entity : entities)
				entity.GetAttribute(name).SetInvalid();

			int count = entities.size();
			String data = "";

			if(count == 1)
				data = "set invalid attribute for one object";
			else if(count > 1)
				data = "set invalid attribute for each of " + count + " entities";

			return new TextString(data);
		}
	}

	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static class suppress extends ModelOperation
	{
		public suppress() {super("suppress", true);}


		@Override
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path", "template_id");
			Operations.AllParams(params, "path", "template_id", "description");

			String template_id_str = params.get("template_id");
			String description = params.get("description");

			if(description == null)
				description = "";

			long template_id = (long)SimpleAttribute.ValueFromStr(template_id_str);

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
		public TypedString Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path", "template_id");
			Operations.AllParams(params, "path", "template_id");

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
