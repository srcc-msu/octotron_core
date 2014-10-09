package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;

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
		super(EAttributeType.SENSOR, parent, name, value);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.SENSOR;
	}

	public boolean Update(Object new_value)
	{
		return super.Update(new_value);
	}
}
