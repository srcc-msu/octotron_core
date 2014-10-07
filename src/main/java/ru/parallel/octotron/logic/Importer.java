package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

public class Importer implements Runnable
{
	private final ModelEntity entity;
	private final SimpleAttribute attribute;

	public Importer(ModelEntity entity, SimpleAttribute attribute)
	{
		this.entity = entity;
		this.attribute = attribute;
	}

	protected AttributeList<VarAttribute> GetDependant(AttributeList<? extends IModelAttribute> attributes)
	{
		AttributeList<VarAttribute> result = new AttributeList<>();

		for(IModelAttribute attribute : attributes)
		{
			result.addAll(attribute.GetDependant());
		}

		return result;
	}

	public AttributeList<VarAttribute> ProcessVaryings(AttributeList<SensorAttribute> changed)
	{
		AttributeList<VarAttribute> result = new AttributeList<>();

		AttributeList<VarAttribute> dependant_varyings = GetDependant(changed);

		do
		{
			for(VarAttribute dependant_varying : dependant_varyings)
			{
				dependant_varying.Update();
				result.add(dependant_varying);
			}

			dependant_varyings = GetDependant(dependant_varyings);
		}
		while(dependant_varyings.size() != 0);

		return result;
	}

	@Override
	public void run()
	{
		SensorAttribute sensor = entity.GetSensor(attribute.GetName());
		sensor.Update(attribute.GetValue());

		AttributeList<SensorAttribute> sensors = new AttributeList<SensorAttribute>();
		sensors.add(sensor);

		AttributeList<VarAttribute> result = ProcessVaryings(sensors);
	}
}
