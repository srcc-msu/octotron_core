/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.attributes;


import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;

public class History
{
	private static final int HISTORY_SIZE = 3;

	public static class Entry
	{
		final Object value;
		final long ctime;

		public Entry(Object value, long ctime)
		{
			this.value = value;
			this.ctime = ctime;
		}
	}

	private final LinkedList<Entry> history = new LinkedList<>();

	@Nullable
	public Entry GetLast()
	{
		if(history.size() > 0)
			return history.getFirst();
		else
			return null;
	}

	public Collection<Entry> Get()
	{
		return history;
	}

	public void Add(Object value, long ctime)
	{
		history.addFirst(new Entry(value, ctime));

		if(history.size() > HISTORY_SIZE)
			history.removeLast();
	}
}
