/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.generators.tmpl.*;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelEntityBuilder<T extends ModelEntity>
{
	protected final T entity;

	ModelEntityBuilder(T entity)
	{
		this.entity = entity;
	}

//--------

	public void MakeDependencies()
	{
		for(Reaction reaction : entity.GetReaction())
			for(String trigger_name : reaction.GetTemplate().GetTriggerNames())
			{
				Trigger trigger = entity.GetTrigger(trigger_name);

				trigger.AddDependOnMe(reaction);
				reaction.AddIDependOn(trigger);
			}

		for(Var var : entity.GetVar())
			for(Attribute dependant : var.GetRule().GetDependency(entity))
			{
				dependant.AddDependOnMe(var);
				var.AddIDependOn(dependant);
			}

		for(Trigger trigger : entity.GetTrigger())
			for(Attribute dependant : trigger.GetCondition().GetDependency(entity))
			{
				dependant.AddDependOnMe(trigger);
				trigger.AddIDependOn(dependant);
			}
	}

//--------

	private void CheckAddAttribute(Attribute attribute)
	{
		if(entity.TestAttribute(attribute.GetName()))
			throw new ExceptionModelFail("attribute already declared: " + attribute.GetName());

		entity.attributes_map.put(attribute.GetName(), attribute);
	}

//--------

	public void DeclareConst(String name, Object value)
	{
		Value converted_value = Value.Construct(value);

		Const attribute = new Const(entity, name, converted_value);

		CheckAddAttribute(attribute);
		entity.const_map.put(name, attribute);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterConst(attribute);
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

//--------

	static final Map<ConstTemplate, Const> static_cache = new HashMap<>();

	public void DeclareStatic(ConstTemplate constant)
	{
		Const cached = static_cache.get(constant);

		if(cached == null)
		{
			cached = new Const(entity, constant.name, Value.Construct(constant.value));
			static_cache.put(constant, cached);
		}

		CheckAddAttribute(cached);
		entity.const_map.put(constant.name, cached);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterConst(cached);
	}

	public void DeclareStatic(Iterable<ConstTemplate> constants)
	{
		for(ConstTemplate constant : constants)
			DeclareStatic(constant);
	}

//--------

	public void DeclareSensor(String name, long update_time, Object default_value)
	{
		Value converted_value = Value.Construct(default_value);

		DeclareSensor(name, update_time, converted_value);
	}

	public void DeclareSensor(String name, long update_time, Value value)
	{
		Sensor sensor = new Sensor(entity, name, update_time, value);

		CheckAddAttribute(sensor);
		entity.sensor_map.put(name, sensor);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterSensor(sensor);
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

//--------

	public void DeclareVar(String name, Rule rule)
	{
		Var var = new Var(entity, name, rule);

		CheckAddAttribute(var);
		entity.var_map.put(name, var);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterVar(var);
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

//--------

	public void DeclareTrigger(String name, Rule condition)
	{
		Trigger var = new Trigger(entity, name, condition);

		CheckAddAttribute(var);
		entity.trigger_map.put(name, var);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterTrigger(var);
	}

	public void DeclareTrigger(Iterable<TriggerTemplate> triggers)
	{
		for(TriggerTemplate trigger : triggers)
			DeclareTrigger(trigger.name, trigger.condition);
	}

	public void DeclareTrigger(TriggerTemplate trigger)
	{
		DeclareTrigger(trigger.name, trigger.condition);
	}

//--------

	public void DeclareReaction(String name, ReactionAction action)
	{
		Reaction reaction = new Reaction(entity, name, action);

		CheckAddAttribute(reaction);
		entity.reaction_map.put(name, reaction);

		ServiceLocator.INSTANCE.GetPersistenceService().RegisterReaction(reaction);
	}

	public void DeclareReaction(ReactionTemplate reaction)
	{
		DeclareReaction(reaction.name, reaction.action);
	}

	public void DeclareReaction(List<ReactionTemplate> reactions)
	{
		for(ReactionTemplate reaction : reactions)
			DeclareReaction(reaction);
	}
}
