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

import java.util.Collection;

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

	protected Collection<VarAttribute> GetDependFromList(Collection<? extends IModelAttribute> attributes)
	{
		Collection<VarAttribute> result = new AttributeList<>();

		for(IModelAttribute attribute : attributes)
		{
			result.addAll(attribute.GetDependFromMe());
		}

		return result;
	}

	public Collection<IModelAttribute> ProcessVars(SensorAttribute changed)
	{
		Collection<IModelAttribute> result = new AttributeList<>();

		Collection<VarAttribute> depend_from_changed = changed.GetDependFromMe();

		do
		{
			for(VarAttribute var : depend_from_changed)
			{
				if(var.Update())
					result.add(var);
			}

			depend_from_changed = GetDependFromList(depend_from_changed);
		}
		while(depend_from_changed.size() != 0);

		return result;
	}

	@Override
	public void run()
	{
		SensorAttribute sensor = entity.GetSensor(name);
		sensor.Update(value);

		Collection<IModelAttribute> result = ProcessVars(sensor);

		result.add(sensor);

		controller.CheckReactions(result);
	}
}
