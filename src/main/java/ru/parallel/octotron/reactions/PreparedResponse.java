/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.reactions;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.exec.GlobalSettings;
import ru.parallel.octotron.primitive.EEntityType;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.OctoObjectList;
import ru.parallel.utils.FileUtils;


public class PreparedResponse
{
	private static final char ARG_CHAR = '$';

	private final OctoResponse response;

	private final List<String[]> composed_commands = new LinkedList<>();

	private String attribute_values;
	private String parent_attribute_values;

	private final long timestamp;

	public PreparedResponse(OctoResponse response, OctoEntity entity, long timestamp)
	{
		this.response = response;
		this.timestamp = timestamp;

		СomposeAttributes(entity);
		СomposeParentAttributes(entity);
		СomposeCommands(entity);
	}

	private String GetAttrStr(String[] attributes, OctoEntity entity)
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

	private void СomposeAttributes(OctoEntity entity)
	{
		String[] print_attributes = response.GetPrintAttributes();

		if(print_attributes.length == 0)
		{
			attribute_values = "";
			return;
		}

		attribute_values = GetAttrStr(print_attributes, entity);
	}

	private void СomposeParentAttributes(OctoEntity entity)
	{
		String[] print_attributes = response.GetParentPrintAttributes();

		if(print_attributes.length == 0)
		{
			parent_attribute_values = "";
			return;
		}

		if(entity.GetUID().getType() != EEntityType.OBJECT)
			throw new ExceptionModelFail("only objects have a parent");

		OctoObject p_entity = (OctoObject)entity;

		OctoObjectList parents = p_entity.GetInNeighbors("type", "contain");

		if(parents.size() > 1)
			System.err.println("could not traceback parents - ambiguity");

		if(parents.size() == 0)
			System.err.println("could not traceback parents - no parents");

		OctoEntity parent = parents.Only();

		parent_attribute_values = GetAttrStr(print_attributes, parent);
	}

	private void СomposeCommands(OctoEntity entity)
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
	public void Invoke(GlobalSettings settings)
		throws ExceptionSystemError, ExceptionModelFail
	{
		for(String[] command : composed_commands)
		{
			String actual_name = settings.GetScriptByKey(command[0]);

			if(actual_name == null)
				throw new ExceptionModelFail("there is no scipt with key: " + command[0]);

			String[] full_command = ArrayUtils.addAll(command
				, Long.toString(timestamp)
				, response.GetStatus().toString()
				, response.GetDescription()
				, attribute_values
				, parent_attribute_values);


			full_command[0] = actual_name;
			FileUtils.ExecSilent(full_command);
		}

		for(String log_key : response.GetLogKeys())
		{
			String fname = settings.GetLogByKey(log_key);

			if(fname == null)
				throw new ExceptionModelFail("there is no logging entry with key: " + log_key);

			FileLog file = new FileLog(fname);
			file.Log(GetFullString());
			file.Close();
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
