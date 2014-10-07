/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.utils.FileUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreparedResponse implements Runnable
{
	private final static Logger LOGGER = Logger.getLogger("octotron");

	private static final char ARG_CHAR = '$';

	private final Response response;

	private final List<String[]> composed_commands = new LinkedList<>();
	private final GlobalSettings settings;

	private String attribute_values;
	private String parent_attribute_values;

	private final long timestamp;

	public PreparedResponse(Response response, ModelEntity entity, long timestamp, GlobalSettings settings)
	{
		this.response = response;
		this.timestamp = timestamp;
		this.settings = settings;

		СomposeAttributes(entity);
		СomposeParentAttributes(entity);
		СomposeCommands(entity);
	}

	private static String GetAttrStr(String[] attributes, ModelEntity entity)
	{
		StringBuilder str = new StringBuilder();

		if(attributes.length > 0)
		{
			String prefix = "";

			for(String attr : attributes)
			{
				if(entity.TestAttribute(attr))
				{
					Object value = entity.GetAttribute(attr).GetValue();

					str.append(prefix)
						.append(SimpleAttribute.ValueToStr(attr)).append(':')
						.append(SimpleAttribute.ValueToStr(value));

					prefix = ",";
				}
			}
		}

		return str.toString();
	}

	private void СomposeAttributes(ModelEntity entity)
	{
		String[] print_attributes = response.GetPrintAttributes();

		if(print_attributes.length == 0)
		{
			attribute_values = "";
			return;
		}

		attribute_values = GetAttrStr(print_attributes, entity);
	}

	private void СomposeParentAttributes(ModelEntity entity)
	{
		String[] print_attributes = response.GetParentPrintAttributes();

		if(print_attributes.length == 0)
		{
			parent_attribute_values = "";
			return;
		}

		if(entity.GetType() != EEntityType.OBJECT)
			throw new ExceptionModelFail("only objects have a parent");

		ModelObject p_entity = (ModelObject)entity;

		ModelObjectList parents = p_entity.GetInLinks().Filter("type", "contain").Source();

		if(parents.size() > 1)
			LOGGER.log(Level.WARNING, "could not traceback parents - ambiguity");

		if(parents.size() == 0)
			LOGGER.log(Level.WARNING, "could not traceback parents - no parents");

		ModelObject parent = parents.Only();

		parent_attribute_values = GetAttrStr(print_attributes, parent);
	}

	private void СomposeCommands(ModelEntity entity)
	{
		for(String[] command : response.GetCommands())
		{
			String[] composed_command = new String[command.length];

			for(int i = 0; i < command.length; i++)
			{
				if(command[i].length() > 1 && command[i].charAt(0) == PreparedResponse.ARG_CHAR)
					composed_command[i] = entity.GetAttribute(command[i].substring(1)).GetValue().toString();
				else
					composed_command[i] = command[i];
			}

			composed_commands.add(composed_command);
		}
	}

/**
 * add attributes to the command and replace the command key by actual file<br>
 * */
	@Override
 	public void run()
	{
		for(String[] command : composed_commands)
		{
			String actual_name = settings.GetScriptByKey(command[0]);

			if(actual_name == null)
				throw new ExceptionModelFail("there is no script with key: " + command[0]);

			try
			{
				FileUtils.ExecSilent(actual_name
					, Long.toString(timestamp)
					, response.GetStatus().toString()
					, response.GetDescription()
					, attribute_values
					, parent_attribute_values);
			}
			catch(ExceptionSystemError e)
			{
				LOGGER.log(Level.SEVERE, "could not invoke reaction script", e);
			}
		}

		for(String log_key : response.GetLogKeys())
		{
			String fname = settings.GetLogByKey(log_key);

			if(fname == null)
				throw new ExceptionModelFail("there is no logging entry with key: " + log_key);

			try
			{
				FileLog file = new FileLog(fname);
				file.Log(GetFullString());
				file.Close();
			}
			catch(ExceptionSystemError e)
			{
				LOGGER.log(Level.WARNING, "could not create a log entry", e);
			}
		}
	}

	public String GetFullString()
	{
		return "{ "
			+        SimpleAttribute.ValueToStr("time")   + ":" + Long.toString(timestamp)
			+ ", " + SimpleAttribute.ValueToStr("event")  + ":" + SimpleAttribute.ValueToStr(response.GetStatus().toString())
			+ ", " + SimpleAttribute.ValueToStr("msg")    + ":" + SimpleAttribute.ValueToStr(response.GetDescription())
			+ ", " + SimpleAttribute.ValueToStr("this")   + ":" + "{" + attribute_values + "}"
			+ ", " + SimpleAttribute.ValueToStr("parent") + ":" + "{" + parent_attribute_values + "}"
			+ "}";
	}
}
