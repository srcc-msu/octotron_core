/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.logic;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;

import ru.parallel.octotron.exec.ExecutionController;

public class Importer implements Runnable
{
	private final ExecutionController controller;
	private final ModelEntity entity;

	private final String name;
	private final Value value;

	public Importer(ExecutionController controller, ModelEntity entity, String name, Value value)
	{
		this.controller = controller;
		this.entity = entity;

		this.name = name;
		this.value = value;
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

	public AttributeList<IModelAttribute> ProcessVars(SensorAttribute changed)
	{
		AttributeList<IModelAttribute> result = new AttributeList<>();

		AttributeList<VarAttribute> dependant_varyings = changed.GetDependant();

		do
		{
			for(VarAttribute dependant_varying : dependant_varyings)
			{
				if(dependant_varying.Update())
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
		SensorAttribute sensor = entity.GetSensor(name);
		sensor.Update(value);

		AttributeList<IModelAttribute> result = ProcessVars(sensor);

		result.add(sensor);

		controller.CheckReactions(result);
	}
}
