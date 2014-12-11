/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.generators.tmpl.ConstTemplate;
import ru.parallel.octotron.generators.tmpl.ReactionTemplate;
import ru.parallel.octotron.core.logic.Rule;

import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.tmpl.SensorTemplate;
import ru.parallel.octotron.generators.tmpl.VarTemplate;

import java.util.List;

public abstract class ModelEntityBuilder<T extends ModelEntity>
{
	protected final T entity;
	protected final ModelService service;

	ModelEntityBuilder(ModelService service, T entity)
	{
		this.service = service;
		this.entity = entity;
	}

	public void AddReaction(ReactionTemplate reaction_template)
	{
		entity.GetAttribute(reaction_template.GetCheckName())
			.GetBuilder(service).AddReaction(reaction_template);
	}

	public void AddReaction(List<ReactionTemplate> reactions)
	{
		for(ReactionTemplate reaction : reactions)
			AddReaction(reaction);
	}

//------------------------

	public void DeclareConst(String name, Object value)
	{
		Value converted_value = Value.Construct(value);

		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		ConstAttribute attribute = new ConstAttribute(entity, name, converted_value);

		entity.attributes_map.put(name, attribute);
		entity.const_map.put(name, attribute);

		service.GetUpdateService().RegisterConst(attribute);
	}

	public void DeclareConst(ConstTemplate constant)
	{
		DeclareConst(constant.name, constant.value);
	}

	public void DeclareConst(Iterable<ConstTemplate> constants)
	{
		for(ConstTemplate constant : constants)
			DeclareConst(constant);
	}

//------------------------

	public void DeclareSensor(String name, long update_time)
	{
		DeclareSensor(name, update_time, Value.undefined);
	}

	public void DeclareSensor(String name, long update_time, Object default_value)
	{
		Value converted_value = Value.Construct(default_value);

		DeclareSensor(name, update_time, converted_value);
	}

	public void DeclareSensor(String name, long update_time, Value value)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		SensorAttribute sensor = new SensorAttribute(entity, name, update_time, value);

		entity.attributes_map.put(name, sensor);
		entity.sensor_map.put(name, sensor);

		service.GetUpdateService().RegisterSensor(sensor);
	}

	public void DeclareSensor(SensorTemplate sensor)
	{
		DeclareSensor(sensor.name, sensor.update_time, sensor.value);
	}

	public void DeclareSensor(Iterable<SensorTemplate> sensors)
	{
		for(SensorTemplate sensor : sensors)
			DeclareSensor(sensor);
	}

//------------------------

	public void DeclareVar(String name, Rule rule)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		VarAttribute var = new VarAttribute(entity, name, rule);

		entity.attributes_map.put(name, var);
		entity.var_map.put(name, var);

		service.GetUpdateService().RegisterVar(var);
	}

	public void DeclareVar(Iterable<VarTemplate> vars)
	{
		for(VarTemplate var : vars)
			DeclareVar(var.name, var.rule);
	}

	public void DeclareVar(VarTemplate var)
	{
		DeclareVar(var.name, var.rule);
	}
}
