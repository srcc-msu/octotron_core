package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

public class SensorAttribute extends AbstractModAttribute
{
	public static class SensorAttributeBuilder extends AbstractModAttributeBuilder<SensorAttribute>
	{
		SensorAttributeBuilder(SensorAttribute attribute)
		{
			super(attribute);
		}
	}

	@Override
	public SensorAttributeBuilder GetBuilder()
	{
		return new SensorAttributeBuilder(this);
	}

	public SensorAttribute(ModelEntity parent, String name, Object value)
	{
		super(parent, name, value);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.SENSOR;
	}

	public void Update(Object new_value)
	{
		throw new ExceptionModelFail("NIY");
	}
}
