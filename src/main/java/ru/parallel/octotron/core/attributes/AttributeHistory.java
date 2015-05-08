/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;

import java.util.Collection;
import java.util.LinkedList;

public class AttributeHistory
{
	private static final int HISTORY_SIZE = 1;

	public static class Entry
	{
		final Value value;
		final long ctime;

		public Entry(Value value, long ctime)
		{
			this.value = value;
			this.ctime = ctime;
		}
	}

	private final LinkedList<Entry> history = new LinkedList<>();

	public Entry GetLast()
	{
		if(history.size() > 0)
			return history.getLast();
		else
			return new Entry(Value.undefined, 0);
	}

	public Collection<Entry> Get()
	{
		return history;
	}

	public void Add(Value value, long ctime)
	{
		history.addLast(new Entry(value, ctime));

		if(history.size() > HISTORY_SIZE)
			history.removeFirst();
	}
}
