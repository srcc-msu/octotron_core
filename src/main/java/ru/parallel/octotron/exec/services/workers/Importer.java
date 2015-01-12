/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.exec.services.workers;

import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.exec.services.UpdateService;

public class Importer implements Runnable
{
	private final UpdateService update_service;

	private final ModelEntity entity;

	private final String name;
	private final Value value;

	public Importer(UpdateService update_service, ModelEntity entity, String name, Value value)
	{
		this.update_service = update_service;
		this.entity = entity;

		this.name = name;
		this.value = value;
	}

	@Override
	public void run()
	{
		SensorAttribute sensor = entity.GetSensor(name);

		sensor.Update(value);

		update_service.Update(sensor, true);
	}
}
