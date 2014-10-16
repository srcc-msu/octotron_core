/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelData;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.JsonString;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.Context;
import ru.parallel.octotron.http.ParsedPath;
import ru.parallel.octotron.http.PathParser;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.FileUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreparedResponse implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final Context context;
	private final Response response;

	private final List<String[]> compiled_commands = new LinkedList<>();
	private final List<String> compiled_messages = new LinkedList<>();
	private final List<String> specials = new LinkedList<>();

	private Map<String, Object> attributes = new HashMap<>();
	private final List<Map<String, Object>> parent_attributes = new LinkedList<>();

	private final long timestamp;

	public PreparedResponse(ModelEntity entity, Reaction reaction, Response response, long timestamp, Context context)
	{
		this.response = response;
		this.timestamp = timestamp;
		this.context = context;

		ComposeAttributes(entity);
		ComposeParentAttributes(entity);
		ComposeMessages(entity);
		ComposeSpecialMessages(entity, reaction);
		ComposeCommands(entity);
	}

	private void ComposeSpecialMessages(ModelEntity entity, Reaction reaction)
	{
		String suppress = String.format("to suppress this reaction: http://%s:%d/modify/suppress?path=obj(AID==%d)&template_id=%d&description=spam"
			, context.settings.GetHost(), context.settings.GetPort()
			, entity.GetID(), reaction.GetTemplate().GetID());
		specials.add(suppress);

		String show_all = String.format("to view all suppressed reactions: http://%s:%d/view/show_suppressed"
			, context.settings.GetHost(), context.settings.GetPort());
		specials.add(show_all);
	}

	private static final String NOT_FOUND = "<%s:not_found>";

	private static Map<String, Object> GetAttributes(ModelEntity entity, Iterable<String> names)
	{
		Map<String, Object> result = new HashMap<>();

		for(String name : names)
		{
			Object value;

			if(entity.TestAttribute(name))
				value = entity.GetAttribute(name).GetValue();
			else
				value = String.format(NOT_FOUND, name);

			result.put(name, value);
		}

		return result;
	}

	private void ComposeParentAttributes(ModelEntity entity)
	{
		if(entity.GetType() != EEntityType.OBJECT)
			throw new ExceptionModelFail("only objects have a parent");

		for(ModelEntity parent : ((ModelObject)entity).GetInNeighbors())
		{
			parent_attributes.add(GetAttributes(parent, response.GetAttributes()));
		}
	}

	private void ComposeAttributes(ModelEntity entity)
	{
		attributes = GetAttributes(entity, response.GetAttributes());
	}

	private static Pattern PATTERN_NAME_PATH = Pattern.compile("\\{([^:{}]+):([^:{}]+)\\}");
	private static Pattern PATTERN_NAME = Pattern.compile("\\{([^{}]+)\\}");

	public static String ReplaceWithPath(String path, String name, ModelEntity entity, ModelData model_data)
		throws ExceptionParseError
	{
		String where = String.format("obj(AID==%d).", entity.GetID());

		ParsedPath parsed_path = PathParser.Parse(where + path);

		ModelList<? extends ModelEntity, ?> targets = parsed_path.Execute(
			ModelList.Single(entity), model_data);

		String result = "";

		if(targets.size() > 0)
		{
			String prefix = "";
			for(ModelEntity target : targets)
			{
				result += prefix + target.GetAttribute(name).GetStringValue();
				prefix = ",";
			}

			return result.replaceAll("\"", "");
		}
		else
			return String.format(NOT_FOUND, name);
	}

	public static String ReplaceSimple(String name, ModelEntity entity)
	{
		if(entity.TestAttribute(name))
			return entity.GetAttribute(name).GetStringValue().replaceAll("\"", "");
		else
			return String.format(NOT_FOUND, name);
	}

	public static String ComposeString(String string, ModelEntity entity, ModelData model_data)
		throws ExceptionParseError
	{
		String result = string;

		Matcher matcher = PATTERN_NAME_PATH.matcher(result);

		while(matcher.find())
		{
			String path = matcher.group(1);
			String name = matcher.group(2);
			String all = matcher.group(0);

			result = result.replace(all, ReplaceWithPath(path, name, entity, model_data));
		}

		Matcher simple_matcher = PATTERN_NAME.matcher(result);

		while(simple_matcher.find())
		{
			String name = simple_matcher.group(1);
			String all = simple_matcher.group(0);

			result = result.replace(all, ReplaceSimple(name, entity));
		}

		return result;
	}

	private void ComposeMessages(ModelEntity entity)
	{
		for(String message : response.GetMessages())
		{
			try
			{
				compiled_messages.add(ComposeString(message, entity, context.model_data));
			}
			catch (Exception e)
			{
				compiled_messages.add(message);
				LOGGER.log(Level.WARNING, "failed to compile message string: " + message, e);
			}
		}
	}

	/**
	 * must be called after ComposeMessages
	 * */
	private void ComposeCommands(ModelEntity entity)
	{
		for(String key : response.GetCommands().keySet())
		{
			String actual_name = context.settings.GetScriptByKey(key);

			if (actual_name == null)
				throw new ExceptionModelFail("there is no script with key: " + key);

			List<String> result = new LinkedList<>();
			result.add(actual_name);

			String[] params = response.GetCommands().get(key);

			for(String param : params)
			{
				try
				{
					result.add(ComposeString(param, entity, context.model_data));
				}
				catch (ExceptionParseError e)
				{
					LOGGER.log(Level.WARNING, "failed to compile script param: " + param, e);
				}
			}

			result.add(Long.toString(timestamp));
			result.add(response.GetStatus().toString());

			for(String message : compiled_messages)
				result.add(message);

			for(String special : specials)
				result.add(special);

			compiled_commands.add(result.toArray(new String[0]));
		}
	}

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		if(!response.IsSuppress())
		{
			for (String[] command : compiled_commands)
			{
				try
				{
					FileUtils.ExecSilent(command);
				}
				catch (ExceptionSystemError e)
				{
					LOGGER.log(Level.SEVERE, "could not invoke reaction script: " + Arrays.toString(command), e);
				}
			}
		}

		try
		{
			FileLog file = new FileLog(context.settings.GetLogDir());
			file.Log(GetFullString());
			file.Close();
		}
		catch(ExceptionSystemError e)
		{
			LOGGER.log(Level.WARNING, "could not create a log entry", e);
		}
	}

	public String GetFullString()
	{
		Map<String, Object> result = new HashMap<>();

		result.put("time", timestamp);

		result.put("event", response.GetStatus().toString());

		for(int i = 0; i < compiled_messages.size(); i++)
			result.put("msg_" + i, compiled_messages.get(i));

		if(attributes.size() > 0)
			result.put("attributes", new JsonString(AutoFormat.PrintJson(Arrays.asList(attributes))));

		if(parent_attributes.size() > 0)
			result.put("parent_attributes", new JsonString(AutoFormat.PrintJson(parent_attributes)));

//		result.put("this", AutoFormat.PrintJson(Arrays.asList(attribute_values)));

//		result.put("parent", AutoFormat.PrintJson(Arrays.asList(parent_attribute_values)));

		return AutoFormat.PrintJson(Arrays.asList(result));
	}
}
