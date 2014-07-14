/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import org.apache.commons.lang3.ArrayUtils;
import ru.parallel.octotron.core.primitive.EEventStatus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OctoResponse implements Serializable
{
	private static final long serialVersionUID = 4220431577263770147L;

	private final EEventStatus status;

	private final String description;

	private final List<String[]> commands = new LinkedList<>();
	private final List<String> log_keys = new LinkedList<>();

	private String[] print_attributes = new String[0];
	private String[] parent_print_attributes = new String[0];

	public OctoResponse(EEventStatus status, String description)
	{
		this.status = status;
		this.description = description;
	}

	public OctoResponse Log(String log_key) {
		log_keys.add(log_key);

		return this;
	}

	public OctoResponse Exec(String... command)
	{
		commands.add(command);
		return this;
	}

	public OctoResponse Print(String... attributes)
	{
		print_attributes = ArrayUtils.addAll(print_attributes, attributes);
		return this;
	}

	public OctoResponse PrintParent(String... attributes)
	{
		parent_print_attributes = ArrayUtils.addAll(parent_print_attributes, attributes);
		return this;
	}

	public EEventStatus GetStatus()
	{
		return status;
	}

	public String GetDescription()
	{
		return description;
	}

	public List<String[]> GetCommands()
	{
		return commands;
	}

	public List<String> GetLogKeys()
	{
		return log_keys;
	}

	public String[] GetPrintAttributes()
	{
		return print_attributes;
	}

	public String[] GetParentPrintAttributes()
	{
		return parent_print_attributes;
	}
}
