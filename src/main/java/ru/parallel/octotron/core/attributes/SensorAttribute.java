package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;

public class SensorAttribute extends AbstractModAttribute
{
	@Override
	public SensorAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new SensorAttributeBuilder(service, this);
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
