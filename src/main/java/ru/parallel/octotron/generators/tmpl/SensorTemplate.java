package ru.parallel.octotron.generators.tmpl;

public class SensorTemplate
{
	public final String name;
	public final Object value;
	public final long update_time;

	public SensorTemplate(String name, long update_time, Object value)
	{
		this.name = name;
		this.value = value;
		this.update_time = update_time;
	}

	public SensorTemplate(String name, long update_time)
	{
		this(name, update_time, null);
	}
}
