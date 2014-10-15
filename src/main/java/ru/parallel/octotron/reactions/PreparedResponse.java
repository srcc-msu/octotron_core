/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.JsonString;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.utils.AutoFormat;
import ru.parallel.utils.FileUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedResponse implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private final GlobalSettings settings;
	private final Response response;

	private final List<String[]> compiled_commands = new LinkedList<>();
	private final List<String> compiled_messages = new LinkedList<>();
	private final List<String> specials = new LinkedList<>();

	private Map<String, Object> attributes = new HashMap<>();
	private final List<Map<String, Object>> parent_attributes = new LinkedList<>();

	private final long timestamp;

	public PreparedResponse(ModelEntity entity, Reaction reaction, Response response, long timestamp, GlobalSettings settings)
	{
		this.response = response;
		this.timestamp = timestamp;
		this.settings = settings;

		ComposeAttributes(entity);
		ComposeParentAttributes(entity);
		ComposeMessages(entity);
		ComposeSpecialMessages(entity, reaction, settings);
		ComposeCommands(entity);
	}

	/**
	 * for testing
	 * */
	PreparedResponse(ModelEntity entity, Response response, long timestamp)
	{
		this.response = response;
		this.timestamp = timestamp;
		this.settings = null;

		ComposeAttributes(entity);
		ComposeParentAttributes(entity);
		ComposeMessages(entity);
		ComposeCommands(entity);
	}

	private void ComposeSpecialMessages(ModelEntity entity, Reaction reaction, GlobalSettings settings)
	{
		String suppress = String.format("to suppress this reaction: http://%s:%d/modify/suppress?path=obj(AID==%d)&template_id=%d&description=spam"
			, settings.GetHost(), settings.GetPort()
			, entity.GetID(), reaction.GetTemplate().GetID());
		specials.add(suppress);

		String show_all = String.format("to view all suppressed reactions: http://%s:%d/view/show_suppressed"
			, settings.GetHost(), settings.GetPort());
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

	public static String ReplaceOne(String string, ModelEntity entity, int start, int end)
	{
		String name = string.substring(start + 1, end);

		String value;

		if(entity.TestAttribute(name))
			value = entity.GetAttribute(name).GetStringValue();
		else
			value = String.format(NOT_FOUND, name);

		return string.replace("{" + name + "}", value);
	}

	public static String ComposeString(String string, ModelEntity entity)
		throws ExceptionParseError
	{
		String result = string;

		while(true)
		{
			int start = result.indexOf("{");

			if(start == -1)
				break;

			int end = result.indexOf("}");

			if(end <= start)
				throw new ExceptionParseError("wrong string format: " + string);

			result = ReplaceOne(result, entity, start, end);
		}

		return result.replaceAll("\"", "");
	}

	private void ComposeMessages(ModelEntity entity)
	{
		for(String message : response.GetMessages())
		{
			try
			{
				compiled_messages.add(ComposeString(message, entity));
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
			String actual_name = settings.GetScriptByKey(key);

			if (actual_name == null)
				throw new ExceptionModelFail("there is no script with key: " + key);

			List<String> result = new LinkedList<>();
			result.add(actual_name);

			String[] params = response.GetCommands().get(key);

			for(String param : params)
			{
				try
				{
					result.add(ComposeString(param, entity));
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
			FileLog file = new FileLog(settings.GetLogDir());
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
