/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IAttribute;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.ExecutionController;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.logic.RuntimeService;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.AutoFormat.E_FORMAT_PARAM;

import java.util.*;
import java.util.Map.Entry;

/**
 * implementation of all available http operations<br>
 * */
public abstract class Operations
{
	public static class Operation
	{
		private final String name;
		private final boolean is_blocking;
		private final IExec exec;

		public Operation(String name, boolean is_blocking, IExec exec)
		{
			this.name = name;
			this.is_blocking = is_blocking;
			this.exec = exec;
		}

		public String GetName()
		{
			return name;
		}

		public boolean IsBlocking()
		{
			return is_blocking;
		}

		public Object Execute(ExecutionController controller
			, Map<String, String> params
			, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			return exec.Execute(controller, params, entities);
		}
	}

	private interface IExec
	{
		Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError;
	}

	/**
	 * tests if the \\attr declares a proper print format<br>
	 * */
	private static E_FORMAT_PARAM AttrToFormat(String format)
	{
		switch(format)
		{
			case "plain" : return E_FORMAT_PARAM.PLAIN;
			case "json"  : return E_FORMAT_PARAM.JSON;
			case "jsonp" : return E_FORMAT_PARAM.JSONP;
			default      : return E_FORMAT_PARAM.NONE;
		}
	}

	private static List<SimpleAttribute> GetAttributes(Map<String, String> params)
	{

		String attributes_str = params.get("attributes");
		List<SimpleAttribute> attributes = new LinkedList<>();

		if(attributes_str != null)
			for(String name : attributes_str.split(","))
				attributes.add(new SimpleAttribute(name, null));

		return attributes;
	}

	private static E_FORMAT_PARAM GetFormat(Map<String, String> params)
	{
		E_FORMAT_PARAM format = E_FORMAT_PARAM.PLAIN;

		String format_str = params.get("format");

		if(format_str != null)
			format = Operations.AttrToFormat(format_str);

		return format;
	}

	private static void CheckFormat(E_FORMAT_PARAM format, String callback)
		throws ExceptionParseError
	{
		if(format != E_FORMAT_PARAM.JSONP && callback != null)
			throw new ExceptionParseError("callback is reserved argument for jsonp format");

		if(format == E_FORMAT_PARAM.JSONP && callback == null)
			throw new ExceptionParseError("specify a callback function");
	}

	private static List<IModelAttribute> GetAttributes(ModelEntity entity, List<SimpleAttribute> attributes, EAttributeType type)
	{
		List<IModelAttribute> result = new LinkedList<>();

		if(attributes.size() > 0)
		{
			for(SimpleAttribute names : attributes)
			{
				IModelAttribute attribute = entity.GetAttribute(names.GetName());

				if(type == null || attribute.GetType() == type)
					result.add(attribute);
			}
			return result;
		}
		else
		{
			for(IAttribute names : entity.GetAttributes())
			{
				IModelAttribute attribute = entity.GetAttribute(names.GetName());

				if(type == null || attribute.GetType() == type)
					result.add(attribute);
			}
			return result;
		}
	}

	private static void RequiredParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		for(String name : names)
		{
			String value = params.get(name);

			if(value == null)
				throw new ExceptionParseError("missing a required param: " + name);
		}
	}

	private static void AllParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		Set<String> check_set = new HashSet<>(Arrays.asList(names));

		for(String name : params.keySet())
		{
			if(!check_set.contains(name))
				throw new ExceptionParseError("unexpected param: " + name);
		}
	}

	private static void StrictParams(Map<String, String> params, String... names)
		throws ExceptionParseError
	{
		Operations.RequiredParams(params, names);
		Operations.AllParams(params, names);
	}

//----------------------------
//------------VIEW------------
//----------------------------

/**
 * print size of the given list<br>
 * */
	public static final Operation count = new Operation("count", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path");

			String data = String.valueOf(entities.size());
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * prints properties of the given list entities<br>
 * properties and format are taken from params<br>
 * */
	public static final Operation p = new Operation("p", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path");
			Operations.AllParams(params, "path", "format", "callback", "attributes");

			E_FORMAT_PARAM format = GetFormat(params);
			List<SimpleAttribute> attributes = GetAttributes(params);

			String callback = params.get("callback");

			CheckFormat(format, callback);

			if(entities.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				Map<String, Object> map = new HashMap<>();

				for(IAttribute attribute : GetAttributes(entity, attributes, null))
					map.put(attribute.GetName(), attribute.GetValue());

				data.add(map);
			}

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

	public static final Operation p_const = new Operation("p_const", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path");
			Operations.AllParams(params, "path", "format", "callback", "attributes");

			E_FORMAT_PARAM format = GetFormat(params);
			List<SimpleAttribute> attributes = GetAttributes(params);

			String callback = params.get("callback");

			CheckFormat(format, callback);

			if(entities.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				Map<String, Object> map = new HashMap<>();

				for(IAttribute attribute : GetAttributes(entity, attributes, EAttributeType.CONST))
					map.put(attribute.GetName(), attribute.GetValue());

				data.add(map);
			}

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

	public static final Operation p_sensor = new Operation("p_sensor", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path");
			Operations.AllParams(params, "path", "format", "callback", "attributes");

			E_FORMAT_PARAM format = GetFormat(params);
			List<SimpleAttribute> attributes = GetAttributes(params);

			String callback = params.get("callback");

			CheckFormat(format, callback);

			if(entities.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				for(IModelAttribute attribute : GetAttributes(entity, attributes, EAttributeType.SENSOR))
				{
					Map<String, Object> map = new HashMap<>();
					map.put("name", attribute.GetName());
					map.put("value", attribute.GetValue());
					map.put("valid", attribute.CheckValid());
					data.add(map);
				}
			}

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

	public static final Operation p_var = new Operation("p_var", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path");
			Operations.AllParams(params, "path", "format", "callback", "attributes");

			E_FORMAT_PARAM format = GetFormat(params);
			List<SimpleAttribute> attributes = GetAttributes(params);

			String callback = params.get("callback");

			CheckFormat(format, callback);

			if(entities.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty entities lists - nothing to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(ModelEntity entity : entities)
			{
				for(IModelAttribute attribute : GetAttributes(entity, attributes, EAttributeType.VAR))
				{
					Map<String, Object> map = new HashMap<>();
					map.put("name", attribute.GetName());
					map.put("value", attribute.GetValue());
					map.put("valid", attribute.CheckValid());
					data.add(map);
				}
			}

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

	public static final Operation p_react = new Operation("p_react", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path", "name");
			Operations.AllParams(params, "path", "name", "format", "callback", "attributes");

			E_FORMAT_PARAM format = GetFormat(params);

			String name = params.get("name");
			String callback = params.get("callback");

			CheckFormat(format, callback);

			if(entities.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty entities lists - nothing to print");

			if(entities.size() > 1)
				return new RequestResult(E_RESULT_TYPE.ERROR, "too many entities to print");

			List<Map<String, Object>> data = new LinkedList<>();

			for(Reaction reaction : entities.Only().GetAttribute(name).GetReactions())
			{
				Map<String, Object> map = new HashMap<>();

				map.put("AID", reaction.GetID());
				map.put("template_id", reaction.GetTemplate().GetID());
				map.put("attribute_name", reaction.GetTemplate().GetCheckName());
				map.put("attribute_AID", reaction.GetAttribute().GetID());
				map.put("suppress", reaction.GetSuppress());
				map.put("description", reaction.GetDescription());

				map.put("value", reaction.GetTemplate().GetCheckValue());
				map.put("delay_config", reaction.GetTemplate().GetDelay());
				map.put("repeat_config", reaction.GetTemplate().GetRepeat());

				map.put("usr", reaction.GetTemplate().GetResponse().GetMessages());

				map.put("state", reaction.GetState());
				map.put("stat", reaction.GetStat());
				map.put("delay", reaction.GetTemplate().GetDelay());
				map.put("repeat", reaction.GetTemplate().GetRepeat());

				data.add(map);
			}

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

	/**
 * show all markers in model<br>
 * */
	public static final Operation show_suppressed = new Operation("show_suppressed", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params);

			List<Map<String, Object>> result = new LinkedList<>();

			for(Reaction reaction : controller.GetContext().model_service
				.GetSuppressedReactions())
			{
				Map<String, Object> map = new HashMap<>();

				map.put("AID", reaction.GetID());
				map.put("template_id", reaction.GetTemplate().GetID());
				map.put("attribute_name", reaction.GetTemplate().GetCheckName());
				map.put("attribute_AID", reaction.GetAttribute().GetID());
				map.put("suppress", reaction.GetSuppress());
				map.put("description", reaction.GetDescription());
				map.put("usr", reaction.GetTemplate().GetResponse().GetMessages());

				result.add(map);
			}

			return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJson(result));
		}
	});

/**
 * show all reactions in model<br>
 * */
	public static final Operation show_r = new Operation("show_r", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params, "format", "callback");

			E_FORMAT_PARAM format = GetFormat(params);
			String callback = params.get("callback");
			CheckFormat(format, callback);

// TODO
			return new RequestResult(format, null);
		}
	});

/**
 * collects and shows statistics<br>
 * */
	public static final Operation version = new Operation("version", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params, "format", "callback");

			E_FORMAT_PARAM format = GetFormat(params);
			String callback = params.get("callback");
			CheckFormat(format, callback);

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

			return new RequestResult(format, AutoFormat.PrintData(data, format, callback));
		}
	});

//------------------------------
//------------MODIFY------------
//------------------------------

	/**
	 * adds a single import value to import queue<br>
	 * */
	public static final Operation import_token = new Operation("import", false, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = entities.Only();

			if(target.GetSensor(name) == null)
				return new RequestResult(E_RESULT_TYPE.ERROR
					, "sesnor does not exist: " + name);

			controller.Import(target, new SimpleAttribute(name, value));

			return new RequestResult(E_RESULT_TYPE.TEXT
				, "added to import queue");
		}
	});

	/**
	 * adds a single import value to unchecked import queue<br>
	 * */
	public static final Operation unchecked_import_token = new Operation("unchecked_import", false, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
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

				return new RequestResult(E_RESULT_TYPE.TEXT
					, "attribute not found, but registered, import skipped");
			}
			else
			{
				controller.Import(target, new SimpleAttribute(name, value));
			}

			return new RequestResult(E_RESULT_TYPE.TEXT
				, "added to unchecked import queue");
		}
	});

/**
 * set invalid state to all given entities<br>
 * */
	public static final Operation set_valid = new Operation("set_valid", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
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

			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * set invalid state to all given entities<br>
 * */
	public static final Operation set_invalid = new Operation("set_invalid", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
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

			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * adds the marker to the all given entities<br>
 * */
	public static final Operation suppress = new Operation("suppress", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
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

			return new RequestResult(E_RESULT_TYPE.TEXT, res);
		}
	});


	/**
	 * adds the marker to the all given entities<br>
	 * */
	public static final Operation unsuppress = new Operation("unsuppress", true
		, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
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

			return new RequestResult(E_RESULT_TYPE.TEXT, res);
		}
	});

//-------------------------------
//------------CONTROL------------
//-------------------------------

	/**
	 * asks the execution control to stop the main process<br>
	 * */
	public static final Operation quit = new Operation("quit", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			controller.SetExit(true);

			return new RequestResult(E_RESULT_TYPE.TEXT, "quiting now");
		}
	});

	/**
	 * asks the execution control to perform a self-test<br>
	 * */
	public static final Operation selftest = new Operation("selftest", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			Map<String, Object> result = RuntimeService.PerformSelfTest(controller);

			return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJson(Collections.singleton(result)));
		}
	});

 /**
  * asks the execution control to change mode<br>
  * currently there is only one mode - silent<br>
  * no reactions will be invoked in silent mode<br>
  * */
	public static final Operation mode = new Operation("mode", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "silent");

			String mode_str = params.get("silent");

			boolean mode = (boolean)SimpleAttribute.ValueFromStr(mode_str);

			controller.SetSilent(mode);

			if(mode)
				return new RequestResult(E_RESULT_TYPE.TEXT
					, "silent mode activated - no reactions will be invoked");
			else
				return new RequestResult(E_RESULT_TYPE.TEXT
					, "silent mode deactivated");
		}
	});

/**
 * collects and shows current model snapshot<br>
 * */
	public static final Operation snapshot = new Operation("snapshot", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params, "format", "callback");

			E_FORMAT_PARAM format = GetFormat(params);

			String callback = params.get("callback");
			CheckFormat(format, callback);

			List<Map<String, Object>> data = RuntimeService.MakeSnapshot(controller.GetContext().model_data);

			return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintData(data, format, callback));
		}
	});

/**
 * collects and shows statistics<br>
 * */
	public static final Operation stat = new Operation("stat", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			String result = AutoFormat.PrintNL(controller.GetStat());

			return new RequestResult(E_RESULT_TYPE.TEXT, result);
		}
	});

/**
 * collects and shows attributes, which were modified long ago<br>
 * param - for how long (in seconds)<br>
 * */
	public static final Operation mod_time = new Operation("mod_time", true, new IExec()
	{
		@Override
		public Object Execute(ExecutionController controller, Map<String, String> params, ModelList<? extends ModelEntity, ?> entities)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "interval");

			String value_str = params.get("interval");
			long interval = (long)SimpleAttribute.ValueFromStr(value_str);

			double time;
			List<Map<String, Object>> result;

			try
			{
				result = RuntimeService.CheckModTime(controller.GetContext(), interval);
			}
			catch (ExceptionModelFail e)
			{
				throw new ExceptionParseError(e);
			}

			return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJson(result));
		}
	});

}
