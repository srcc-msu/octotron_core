package ru.parallel.octotron.triggers;

import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.logic.TriggerCondition;
import ru.parallel.octotron.core.model.ModelEntity;

public class NotEquals extends TriggerCondition
{
	private final String name;
	private final Value value;

	public NotEquals(String name, Object value)
	{
		this.name = name;
		this.value = Value.Construct(value);
	}

	@Override
	public boolean Check(ModelEntity entity)
	{
		return entity.GetAttribute(name).ne(value);
	}
}
