/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

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

	public void DeclareConst(String name, Object value)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		ConstAttribute attribute = new ConstAttribute(entity, name, SimpleAttribute.ConformType(value));

		entity.attributes_map.put(name, attribute);
		entity.const_map.put(name, attribute);

		service.RegisterConst(attribute);
	}

	public void DeclareConst(SimpleAttribute attribute)
	{
		DeclareConst(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareConst(Iterable<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConst(attribute);
	}

	public void DeclareSensor(String name, Object default_value)
	{
		DeclareSensor(name, default_value, -1);
	}

	public void DeclareSensor(String name, Object default_value, long update_time)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		SensorAttribute sensor = new SensorAttribute(entity, name, SimpleAttribute.ConformType(default_value), update_time);

		entity.attributes_map.put(name, sensor);
		entity.sensor_map.put(name, sensor);

		service.RegisterSensor(sensor);
	}

	public void DeclareSensor(SimpleAttribute attribute)
	{
		DeclareSensor(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareSensor(Iterable<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareSensor(attribute);
	}

	public void DeclareVar(String name, Rule rule)
	{
		if(entity.TestAttribute(name))
			throw new ExceptionModelFail("attribute already declared: " + name);

		VarAttribute var = new VarAttribute(entity, name, rule);

		entity.attributes_map.put(name, var);
		entity.var_map.put(name, var);

		service.RegisterVar(var);
	}

	public void DeclareVar(Iterable<SimpleAttribute> rules)
	{
		for(SimpleAttribute pair : rules)
			DeclareVar(pair.GetName(), (Rule)(pair.GetValue()));
	}
}
