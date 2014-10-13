package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.exec.ExecutionController;

public class Importer implements Runnable
{
	private final ModelEntity entity;
	private final SimpleAttribute attribute;
	private final ExecutionController controller;

	public Importer(ExecutionController controller, ModelEntity entity, SimpleAttribute attribute)
	{
		this.controller = controller;
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

	public AttributeList<IModelAttribute> ProcessVaryings(SensorAttribute changed)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		AttributeList<VarAttribute> dependant_varyings = changed.GetDependant();

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

		AttributeList<IModelAttribute> result = ProcessVaryings(sensor);

		result.add(sensor);

		controller.CheckReactions(result);
	}
}