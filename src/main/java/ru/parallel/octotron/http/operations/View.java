/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.operations;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IAttribute;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.operations.impl.ModelOperation;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.octotron.reactions.CommonReactions;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.format.ErrorString;
import ru.parallel.utils.format.TextString;
import ru.parallel.utils.format.TypedString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			return new TextString(String.valueOf(entities.size()));
		}
	}

	/**
	 * prints properties of the given list entities<br>
	 * properties and format are taken from params<br>
	 * */
	public static class p2 extends ModelOperation
	{
		public p2()
		{
			super("p2", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params, "attributes");

			List<SimpleAttribute> attributes = Operations.GetAttributes(params);

			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			Object data = Operations.GetAttributes(entities, attributes, null);

			return AutoFormat.PrintJson(data);
		}
	}

	public static class p extends ModelOperation
	{

		public p()
		{
			super("p", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			List<SimpleAttribute> attributes = Operations.GetAttributes(params);

			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			Object data = Operations.GetAttributes(entities, attributes, null);

			return AutoFormat.PrintJson(data);
		}
	}

	public static class p_const extends ModelOperation
	{

		public p_const()
		{
			super("p_const", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params, "attributes");

			List<SimpleAttribute> attributes = Operations.GetAttributes(params);


			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				Map<String, Object> map = new HashMap<>();

				for(IAttribute attribute : Operations.GetAttributes(entity, attributes, EAttributeType.CONST))
					map.put(attribute.GetName(), attribute.GetValue());

				data.add(map);
			}

			return AutoFormat.PrintJson(data);
		}
	}

	public static class p_sensor extends ModelOperation
	{

		public p_sensor()
		{
			super("p_sensor", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params, "attributes");

			List<SimpleAttribute> attributes = Operations.GetAttributes(params);


			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				for(IModelAttribute attribute : Operations.GetAttributes(entity, attributes, EAttributeType.SENSOR))
				{
					Map<String, Object> map = new HashMap<>();
					map.put("name", attribute.GetName());
					map.put("value", attribute.GetValue());
					map.put("valid", attribute.CheckValid());
					data.add(map);
				}
			}

			return AutoFormat.PrintJson(data);
		}
	}

	public static class p_var extends ModelOperation
	{

		public p_var()
		{
			super("p_var", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params, "attributes");

			List<SimpleAttribute> attributes = Operations.GetAttributes(params);


			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				for(IModelAttribute attribute : Operations.GetAttributes(entity, attributes, EAttributeType.VAR))
				{
					Map<String, Object> map = new HashMap<>();
					map.put("name", attribute.GetName());
					map.put("value", attribute.GetValue());
					map.put("valid", attribute.CheckValid());
					data.add(map);
				}
			}

			return AutoFormat.PrintJson(data);
		}
	}

	public static class p_react extends ModelOperation
	{

		public p_react()
		{
			super("p_react", true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "name");
			Operations.AllParams(params, "name", "attributes");


			String name = params.get("name");
			if(entities.size() == 0)
				return new ErrorString("empty entities lists - nothing to print");

			if(entities.size() > 1)
				return new ErrorString("too many entities to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(Reaction reaction : entities.Only().GetAttribute(name).GetReactions())
			{
				data.add(reaction.GetShortRepresentation());
			}

			return AutoFormat.PrintJson(data);
		}
	}

	/**
	 * show all markers in model<br>
	 * */
	public static class show_suppressed extends ModelOperation
	{

		public show_suppressed()
		{
			super("show_suppressed",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params);


			List<Map<String, Object>> result = new LinkedList<>();

			for(Reaction reaction : controller.GetContext().model_service
				.GetSuppressedReactions())
			{
				result.add(reaction.GetShortRepresentation());
			}

			return AutoFormat.PrintJson(result);
		}
	}

	/**
	 * show all reactions in model<br>
	 * */
	public static class show_r extends ModelOperation
	{

		public show_r()
		{
			super("show_r",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params);

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
	public static class version extends ModelOperation
	{

		public version()
		{
			super("version",true);
		}

		@Override
		public TypedString Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params);

			List<Map<String, Object>> data = new LinkedList<>();

			try
			{
				Map<String, String> versions = RuntimeService.GetVersion();

				for(Entry<String, String> entry : versions.entrySet())
				{
					Map<String, Object> version_line = new HashMap<>();
					version_line.put(entry.getKey(), entry.getValue());
					data.add(version_line);
				}
			}
			catch(ExceptionSystemError e)
			{
				throw new ExceptionParseError(e);
			}

			return AutoFormat.PrintJson(data);
		}
	}
}
