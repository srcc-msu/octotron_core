/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.collections.AutoFormat;
import ru.parallel.octotron.core.collections.AutoFormat.E_FORMAT_PARAM;
import ru.parallel.octotron.core.collections.IEntityList;
import ru.parallel.octotron.core.collections.ObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.http.RequestResult.E_RESULT_TYPE;
import ru.parallel.octotron.impl.PersistentStorage;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.utils.JavaUtils;
import ru.parallel.utils.Timer;

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

		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			return exec.Execute(model_service, control, params, objects);
		}
	}

	private interface IExec
	{
		Object Execute(ModelService model_service, ExecutionController control
				, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError;
	}

	/**
	 * tests if the \\attr declares a proper print format<br>
	 * */
	private static E_FORMAT_PARAM AttrToFormat(String format)
	{
		switch(format)
		{
			case "csv"   : return E_FORMAT_PARAM.CSV;
			case "comma" : return E_FORMAT_PARAM.COMMA;
			case "nl"    : return E_FORMAT_PARAM.NL;
			case "json"  : return E_FORMAT_PARAM.JSON;
			case "jsonp" : return E_FORMAT_PARAM.JSONP;
			default      : return E_FORMAT_PARAM.NONE;
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
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "path");

			String data = String.valueOf(objects.size());
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * prints properties of the given list objects<br>
 * properties and format are taken from params<br>
 * */
	public static final Operation p = new Operation("p", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity> objects)
				throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path");
			Operations.AllParams(params, "path", "format", "callback", "attributes");

			E_FORMAT_PARAM format = E_FORMAT_PARAM.NL;

// check if format is specifed

			String format_str = params.get("format");

			if(format_str != null)
				format = Operations.AttrToFormat(format_str);

// check if attributes are specified

			String attributes_str = params.get("attributes");
			List<SimpleAttribute> attributes = new LinkedList<>();

			if(attributes_str != null)
				for(String name : attributes_str.split(","))
					attributes.add(new SimpleAttribute(name, null));

			String callback = params.get("callback");

			if(format != E_FORMAT_PARAM.JSONP && callback != null)
				throw new ExceptionParseError("callback is reserved argument for jsonp format");

			if(format == E_FORMAT_PARAM.JSONP && callback == null)
				throw new ExceptionParseError("specify a callback function");

			if(format == E_FORMAT_PARAM.CSV && attributes.size() == 0)
				throw new ExceptionParseError("specify some fields for CSV format");

			if(objects.size() == 0)
				return new RequestResult(E_RESULT_TYPE.ERROR, "empty objects lists - nothing to print");

			String data = AutoFormat.PrintEntities(objects, attributes, format, callback);

			if(format == E_FORMAT_PARAM.JSON)
				return new RequestResult(E_RESULT_TYPE.JSON, data);
			else if(format == E_FORMAT_PARAM.JSONP)
				return new RequestResult(E_RESULT_TYPE.JSONP, data);
			else if(format == E_FORMAT_PARAM.CSV)
				return new RequestResult(E_RESULT_TYPE.CSV, data);
			else
				return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * print all special(service) attributes of the given objects<br>
 * */
	public static final Operation p_spec = new Operation("p_spec", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.AllParams(params, "path");

			String data = AutoFormat.PrintEntitiesSpecial(objects);
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * show all markers in model<br>
 * */
	public static final Operation show_m = new Operation("show_m", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params);

/*			List<Marker> markers = PersistentStorage.INSTANCE.GetMarkers().GetAll();

			if(markers.isEmpty())
				return new RequestResult(E_RESULT_TYPE.TEXT, "no markers");

			StringBuilder res = new StringBuilder();

			for(Marker marker : markers)
			{
				res.append(String.format("id: %d, owner AID: %d, RID: %d, descr: \"%s\", suppress: %b"
					, marker.GetID(), marker.GetAID(), marker.GetTarget(), marker.GetDescription(), marker.IsSuppress()))
					.append(System.lineSeparator());
			}

			String data = res.toString();
			return new RequestResult(E_RESULT_TYPE.TEXT, data);*/
			return null;
		}
	});

/**
 * show all reactions in model<br>
 * */
	public static final Operation show_r = new Operation("show_r", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params, "format", "callback");

			E_FORMAT_PARAM format = E_FORMAT_PARAM.NL;

// check if format is specifed

			String format_str = params.get("format");

			if(format_str != null)
				format = Operations.AttrToFormat(format_str);

			String callback = params.get("callback");

			if(format == E_FORMAT_PARAM.JSONP && callback == null)
				throw new ExceptionParseError("specify a callback function");

			List<OctoReaction> reactions = PersistentStorage.INSTANCE.GetReactions().GetAll();

			if(reactions.isEmpty())
				return new RequestResult(E_RESULT_TYPE.TEXT, "no reactions");

			List<Map<String, Object>> data = new LinkedList<>();

			for(OctoReaction reaction : reactions)
			{
				Map<String, Object> map = new HashMap<>();

				map.put("RID", reaction.GetID());
				map.put("check_attribute", reaction.GetCheckName());
				map.put("check_value", reaction.GetCheckValue());
				map.put("status", reaction.GetResponse().GetStatus().toString());
				map.put("description", reaction.GetResponse().GetDescription());

				data.add(map);
			}

			if(format == E_FORMAT_PARAM.JSON)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJson(data));
			else if(format == E_FORMAT_PARAM.JSONP)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJsonp(data, callback));
			else if(format == E_FORMAT_PARAM.NL)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintPlain(data));
			else throw new ExceptionParseError("unsuppported format");
		}
	});

/**
 * collects and shows statistics<br>
 * */
	public static final Operation version = new Operation("version", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.RequiredParams(params);
			Operations.AllParams(params, "format", "callback");

			E_FORMAT_PARAM format = E_FORMAT_PARAM.NL;

// check if format is specifed

			String format_str = params.get("format");

			if(format_str != null)
				format = Operations.AttrToFormat(format_str);

			String callback = params.get("callback");

			if(format == E_FORMAT_PARAM.JSONP && callback == null)
				throw new ExceptionParseError("specify a callback function");


			List<OctoReaction> reactions = PersistentStorage.INSTANCE.GetReactions().GetAll();

			if(reactions.isEmpty())
				return new RequestResult(E_RESULT_TYPE.TEXT, "no reactions");

			List<Map<String, Object>> data = new LinkedList<>();

			try
			{
				Map<String, String> versions = control.GetVersion();

				for(Entry<String, String> entry : versions.entrySet())
				{
					Map<String, Object> version_line = new HashMap<>();
					version_line.put(entry.getKey(), entry.getValue());
					data.add(version_line);
				}
			}
			catch (ExceptionSystemError e)
			{
				throw new ExceptionParseError(e);
			}

			if(format == E_FORMAT_PARAM.JSON)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJson(data));
			else if(format == E_FORMAT_PARAM.JSONP)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintJsonp(data, callback));
			else if(format == E_FORMAT_PARAM.NL)
				return new RequestResult(E_RESULT_TYPE.TEXT, AutoFormat.PrintPlain(data));
			else throw new ExceptionParseError("unsuppported format");
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
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = objects.Only();

			if(target.GetUID().getType() != EEntityType.OBJECT)
				throw new ExceptionModelFail("can not import value to a link");
			else
				control.Import(target, new SimpleAttribute(name, value));

			String data = "added to import queue";
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

	/**
	 * adds a single import value to unchecked import queue<br>
	 * */
	public static final Operation unchecked_import_token = new Operation("unchecked_import", false, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
			throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");

			Object value = SimpleAttribute.ValueFromStr(value_str);

			ModelEntity target = objects.Only();

			if(target.GetUID().getType() != EEntityType.OBJECT)
				throw new ExceptionModelFail("can not import value to a link");
			else
				control.UncheckedImport(target, new SimpleAttribute(name, value));

			String data = "added to unchcked import queue";
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * set the given attribute to all given objects<br>
 * */
	public static final Operation set = new Operation("set", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "value");

			String name = params.get("name");
			String value_str = params.get("value");
			Object value = SimpleAttribute.ValueFromStr(value_str);

			for(ModelEntity entity : objects)
			{
				entity.DeclareConstant(new SimpleAttribute(name, value));
			}

			int count = objects.size();
			String data = "";

			if(count == 1)
				data = "set attribute for one object";
			else if(count > 1)
				data = "set attribute for each of " + count + " objects";

			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * set the given attribute to all given objects<br>
 * */
	public static final Operation static_op = new Operation("static", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
		/*	Operations.AllParams(params, "name", "value");
			Operations.RequiredParams(params, "name");

			String name = params.get("name");
			String value_str = params.get("value");

			if(value_str != null)
			{
				Object value = SimpleAttribute.ValueFromStr(value_str);
				GraphService.Get().GetStatic().DeclareAttribute(name, value);
			}
			else
			{
				if(!GraphService.Get().GetStatic().TestAttribute(name))
					return new RequestResult(E_RESULT_TYPE.TEXT, "attribute not found");

				return new RequestResult(E_RESULT_TYPE.TEXT, name + "="
					+ SimpleAttribute.ValueToStr(GraphService.Get().GetStatic().GetAttribute(name).GetValue()));
			}

			return new RequestResult(E_RESULT_TYPE.TEXT, "static attribute set");*/
			return null;
		}
	});

/**
 * set invalid state to all given objects<br>
 * */
	public static final Operation set_valid = new Operation("set_valid", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name");

			String name = params.get("name");

			for(ModelEntity entity : objects)
				entity.GetAttribute(name).SetValid();

			int count = objects.size();
			String data = "";

			if(count == 1)
				data = "set valid attribute for one object";
			else if(count > 1)
				data = "set valid attribute for each of " + count + " objects";

			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * set invalid state to all given objects<br>
 * */
	public static final Operation set_invalid = new Operation("set_invalid", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name");

			String name = params.get("name");

			for(ModelEntity entity : objects)
				entity.GetAttribute(name).SetInvalid();

			int count = objects.size();
			String data = "";

			if(count == 1)
				data = "set invalid attribute for one object";
			else if(count > 1)
				data = "set invalid attribute for each of " + count + " objects";

			return new RequestResult(E_RESULT_TYPE.TEXT, data);
		}
	});

/**
 * adds the marker to the all given objects<br>
 * */
	public static final Operation add_m = new Operation("add_m", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.RequiredParams(params, "path", "reaction_id", "description");
			Operations.AllParams(params, "path", "reaction_id", "description", "suppress");

			String reaction_id_str = params.get("reaction_id");
			String description_str = params.get("description");
			String suppress_str = params.get("suppress");

			long reaction_id = (long)SimpleAttribute.ValueFromStr(reaction_id_str);

			String description = (String)SimpleAttribute.ValueFromStr(description_str);

			boolean suppress = false;
			if(suppress_str != null)
				suppress = (boolean)SimpleAttribute.ValueFromStr(suppress_str);

			StringBuilder res = new StringBuilder();

			//TODO
/*			for(ModelEntity object : objects)
				res.append("marker id=")
					.append(object.AddMarker(reaction_id, description, suppress))
					.append(System.lineSeparator());*/

			return new RequestResult(E_RESULT_TYPE.TEXT, res.toString());
		}
	});

/**
 * delete the marker from object by id<br>
 * */
	public static final Operation del_m = new Operation("del_m", true
		, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "path", "name", "id");
			String id_str = params.get("id");
			String name = params.get("name");

			long id = (long)SimpleAttribute.ValueFromStr(id_str);

			ModelEntity entity = objects.Only();

			entity.DeleteMarker(name, id);

			String data = "deleted marker with id: " + id;
			return new RequestResult(E_RESULT_TYPE.TEXT, data);
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
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			control.SetExit(true);

			return new RequestResult(E_RESULT_TYPE.TEXT, "quiting now");
		}
	});

	/**
	 * asks the execution control to perform a self-test<br>
	 * */
	public static final Operation selftest = new Operation("selftest", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
			throws ExceptionParseError
		{
			Operations.AllParams(params);

			String result = control.PerformSelfTest();

			return new RequestResult(E_RESULT_TYPE.TEXT, result);
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
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "silent");

			String mode_str = params.get("silent");

			boolean mode = (boolean)SimpleAttribute.ValueFromStr(mode_str);

			control.SetSilent(mode);

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
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.AllParams(params);

			double time;
			String result;

Timer.SStart();
			result = control.MakeSnapshot();
time = Timer.SEnd();

			result = "snapshot creation took "+ time + " sec" + System.lineSeparator() + result;

			return new RequestResult(E_RESULT_TYPE.TEXT, result);
		}
	});

/**
 * collects and shows statistics<br>
 * */
	public static final Operation stat = new Operation("stat", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.AllParams(params);

			String result = control.GetStat();

			return new RequestResult(E_RESULT_TYPE.TEXT, result);
		}
	});

/**
 * export full graph information in required format<br>
 * only dot format is supported now<br>
 * works only once per 60 seconds, returns cached result if used too much<br>
 * */
	public static final Operation export = new Operation("export", true, new IExec()
	{
		private static final long EXPORT_THRESHOLD = 60;

		private long last_export = 0;
		private String cached_result = null;

		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.RequiredParams(params, "format");
			Operations.AllParams(params, "format", "path");

			String format = params.get("format");
			String path = params.get("path");

			if(!format.equals("dot"))
				throw new ExceptionParseError("unsupported format: " + format);

//if it is a full graph operation and timelimit is not finished - return a cached value
			if(path == null)
			{
				long cur_time = JavaUtils.GetTimestamp();
				if(cur_time - last_export > EXPORT_THRESHOLD || cached_result == null)
				{
					cached_result = ModelService.ExportDot();
					last_export = cur_time;
				}

				return new RequestResult(E_RESULT_TYPE.TEXT, cached_result);
			}
			else
			{
				if(!(objects instanceof ObjectList))
					return new RequestResult(E_RESULT_TYPE.ERROR, "export works only with objects");

				return new RequestResult(E_RESULT_TYPE.TEXT, ModelService.ExportDot((ObjectList)objects));
			}
		}
	});

/**
 * collects and shows attributes, which were modified long ago<br>
 * param - for how long (in seconds)<br>
 * */
	public static final Operation mod_time = new Operation("mod_time", true, new IExec()
	{
		@Override
		public Object Execute(ModelService model_service, ExecutionController control
			, Map<String, String> params, IEntityList<ModelEntity>objects)
				throws ExceptionParseError
		{
			Operations.StrictParams(params, "interval");

			String value_str = params.get("interval");
			long interval = (long)SimpleAttribute.ValueFromStr(value_str);

			double time;
			String result;

			try
			{
Timer.SStart();
				result = control.CheckModTime(interval);
time = Timer.SEnd();
			}
			catch (ExceptionModelFail e)
			{
				throw new ExceptionParseError(e);
			}

			result = "mod_time creation took "+ time + " sec" + System.lineSeparator() + result;

			return new RequestResult(E_RESULT_TYPE.TEXT, result);
		}
	});

}
