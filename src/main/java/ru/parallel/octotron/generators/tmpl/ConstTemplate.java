package ru.parallel.octotron.generators.tmpl;

import ru.parallel.octotron.core.attributes.Value;

public class ConstTemplate
{
	public final String name;
	public final Value value;

	public ConstTemplate(String name, Object value)
	{
		this.name = name;
		this.value = Value.Construct(value);
	}
}
