package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.Value;

public class SensorTemplate
{
	public final String name;
	public final long update_time;

	public final Value value;

	public SensorTemplate(String name, long update_time, Object value)
	{
		this.name = name;
		this.value = Value.Construct(value);
		this.update_time = update_time;
	}

	public SensorTemplate(String name, long update_time)
	{
		this(name, update_time, Value.undefined);
	}
}
