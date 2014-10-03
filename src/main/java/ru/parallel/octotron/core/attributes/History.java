package ru.parallel.octotron.core.attributes;

import java.util.List;

public class History
{
	public static class Entry
	{
		long ctime;
		Object value;
	}

	List<Entry> history;

	public List<Entry> Get()
	{
		return history;
	}
}
