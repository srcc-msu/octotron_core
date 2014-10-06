package ru.parallel.octotron.core.attributes;

import java.util.LinkedList;
import java.util.List;

public class History
{
	private static final long HISTORY_SIZE = 2;

	public static class Entry
	{
		Object value;
		long ctime;

		public Entry(Object value, long ctime)
		{
			this.value = value;
			this.ctime = ctime;
		}
	}

	List<Entry> history;

	public History()
	{
		history = new LinkedList<>();
	}

	public List<Entry> Get()
	{
		return history;
	}

	public void add(Object value, long ctime)
	{
		history.add(0, new Entry(value, ctime));

		if(history.size() > HISTORY_SIZE)
			history.remove(HISTORY_SIZE);
	}
}
