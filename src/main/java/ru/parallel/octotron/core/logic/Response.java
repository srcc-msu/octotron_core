/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.logic;

import com.google.common.collect.ObjectArrays;
import ru.parallel.octotron.core.primitive.EEventStatus;

import java.io.Serializable;
import java.util.*;

public class Response implements Serializable
{
	private static final long serialVersionUID = 4220431577263770147L;

	private final EEventStatus status;

	private final String[] messages;
	private final Map<String, String[]> commands = new HashMap<>();

	private final List<String> attributes = new LinkedList<>();
	private final List<String> parent_attributes = new LinkedList<>();

	private boolean suppress = false;

	public Response(EEventStatus status, String... messages)
	{
		this.status = status;
		this.messages = messages;
	}

	public Response Exec(String script_key, String... arguments)
	{
		commands.put(script_key, arguments);
		return this;
	}

	public EEventStatus GetStatus()
	{
		return status;
	}

	public String[] GetMessages()
	{
		return messages;
	}

	public List<String> GetAttributes()
	{
		return attributes;
	}

	public List<String> GetParentAttributes()
	{
		return parent_attributes;
	}

	public Map<String, String[]> GetCommands()
	{
		return commands;
	}

	/**
	 * suppress any scripts
	 * does not suppress logging
	 * */
	public Response Suppress(boolean suppress)
	{
		this.suppress = suppress;
		return this;
	}

	public boolean IsSuppress()
	{
		return suppress;
	}

	public Response Print(String... new_attributes)
	{
		attributes.addAll(Arrays.asList(new_attributes));
		return this;
	}

	public Response PrintParent(String... new_attributes)
	{
		parent_attributes.addAll(Arrays.asList(new_attributes));
		return this;
	}
}
