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
import ru.parallel.octotron.exec.services.ReactionService;

import java.util.Collection;

public class Importer implements Runnable
{
	private final ReactionService reaction_service;

	private final ModelEntity entity;

	private final String name;
	private final Value value;

	public Importer(ReactionService reaction_service, ModelEntity entity, String name, Value value)
	{
		this.reaction_service = reaction_service;
		this.entity = entity;

		this.name = name;
		this.value = value;
	}

	@Override
	public void run()
	{
		SensorAttribute sensor = entity.GetSensor(name);
		sensor.Update(value);

		new Updater(reaction_service, sensor, true).run();
	}
}
