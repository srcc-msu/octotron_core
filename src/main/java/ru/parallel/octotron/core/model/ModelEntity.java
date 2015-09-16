/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.Attribute;
import ru.parallel.octotron.core.attributes.impl.*;
import ru.parallel.octotron.core.primitive.*;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.reactions.PreparedResponse;
import ru.parallel.utils.AutoFormat;

import java.util.*;

/**
 * base class for model objects and links
 * stores attributes and provides a way to access them
 * */
public abstract class ModelEntity implements IPresentable
{
	ModelInfo<EModelType> info;

	final Map<String, Attribute> attributes_map = new HashMap<>();

	final Map<String, Const> const_map = new HashMap<>();
	final Map<String, Sensor> sensor_map = new HashMap<>();
	final Map<String, Var> var_map = new HashMap<>();
	final Map<String, Trigger> trigger_map = new HashMap<>();

	final List<Reaction> reactions = new LinkedList<>();

	public ModelEntity(EModelType type)
	{
		this.info = new ModelInfo<>(type);
	}

	public abstract ModelEntityBuilder<?> GetBuilder();

	public Attribute GetAttribute(String name)
	{
		Attribute result = attributes_map.get(name);

		if(result != null)
			return result;

		throw new ExceptionModelFail("attribute not found: " + name + " ; all: " + AutoFormat.FormatJson(GetShortRepresentation()));
	}

	public Collection<Attribute> GetAttributes()
	{
		return attributes_map.values();
	}

	public Collection<? extends Attribute> GetAttributes(EAttributeType type)
	{
		switch(type)
		{
			case CONST:
				return const_map.values();
			case SENSOR:
				return sensor_map.values();
			case VAR:
				return var_map.values();
			case TRIGGER:
				return var_map.values();
			default:
				throw new ExceptionModelFail("unknown attribute type: " + type.toString());
		}
	}

	public Map<String, Object> GetAttributesValues()
	{
		Map<String, Object> result = new HashMap<>();

		for(Attribute attribute : GetAttributes())
		{
			result.put(attribute.GetName(), attribute.GetValue());
		}

		return result;
	}

	public boolean TestAttribute(String name)
	{
		Attribute result = attributes_map.get(name);

		return result != null;
	}

//--------

	public Const GetConstOrNull(String name)
	{
		return const_map.get(name);
	}

	public Const GetConst(String name)
	{
		Const attribute = GetConstOrNull(name);

		if(attribute == null)
			throw new ExceptionModelFail("const not found: " + name);

		return attribute;
	}

	public Collection<Const> GetConst()
	{
		return const_map.values();
	}

//--------

	public Sensor GetSensorOrNull(String name)
	{
		return sensor_map.get(name);
	}

	public Sensor GetSensor(String name)
	{
		Sensor attribute = GetSensorOrNull(name);

		if(attribute == null)
			throw new ExceptionModelFail("sensor not found: " + name);

		return attribute;
	}


	public Collection<Sensor> GetSensor()
	{
		return sensor_map.values();
	}

//--------

	public Trigger GetTriggerOrNull(String name)
	{
		return trigger_map.get(name);
	}

	public Trigger GetTrigger(String name)
	{
		Trigger attribute = GetTriggerOrNull(name);

		if(attribute == null)
			throw new ExceptionModelFail("trigger not found: " + name);

		return attribute;
	}


	public Collection<Trigger> GetTrigger()
	{
		return trigger_map.values();
	}

//--------

	public Var GetVarOrNull(String name)
	{
		return var_map.get(name);
	}

	public Var GetVar(String name)
	{
		Var attribute = GetVarOrNull(name);

		if(attribute == null)
			throw new ExceptionModelFail("var not found: " + name);

		return attribute;
	}

	public Collection<Var> GetVar()
	{
		return var_map.values();
	}

	public Collection<PreparedResponse> GetPreparedResponses()
	{
		List<PreparedResponse> result = new LinkedList<>();

		for(Reaction reaction : reactions)
		{
			PreparedResponse prepared_response = reaction.GetPreparedResponseOrNull();

			if(prepared_response != null && prepared_response.GetResponse().GetStatus() != EEventStatus.RECOVER)
				result.add(prepared_response);
		}

		return result;
	}

	public Collection<Reaction> GetReaction()
	{
		return reactions;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("entity AID", GetInfo().GetID());

		List<Map<String, Object>> const_list = new LinkedList<>();

		for(Const attribute : GetConst())
		{
			const_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> sensor_list = new LinkedList<>();
		for(Sensor attribute : GetSensor())
		{
			sensor_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> var_list = new LinkedList<>();
		for(Var attribute : GetVar())
		{
			var_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> trigger_list = new LinkedList<>();
		for(Trigger attribute : GetTrigger())
		{
			trigger_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> reaction_list = new LinkedList<>();
		for(Reaction attribute : GetReaction())
		{
			reaction_list.add(attribute.GetShortRepresentation());
		}

		result.put("const", const_list);
		result.put("sensor", sensor_list);
		result.put("var", var_list);
		result.put("trigger", trigger_list);
		result.put("reaction", reaction_list);

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("entity AID", GetInfo().GetID());

		List<Map<String, Object>> const_list = new LinkedList<>();

		for(Const attribute : GetConst())
		{
			const_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> sensor_list = new LinkedList<>();
		for(Sensor attribute : GetSensor())
		{
			sensor_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> var_list = new LinkedList<>();
		for(Var attribute : GetVar())
		{
			var_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> trigger_list = new LinkedList<>();
		for(Trigger attribute : GetTrigger())
		{
			trigger_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> reaction_list = new LinkedList<>();
		for(Reaction attribute : GetReaction())
		{
			reaction_list.add(attribute.GetLongRepresentation());
		}

		result.put("const", const_list);
		result.put("sensor", sensor_list);
		result.put("var", var_list);
		result.put("trigger", trigger_list);
		result.put("reaction", reaction_list);

		return result;
	}

	@Override
	public Map<String, Object> GetRepresentation(boolean verbose)
	{
		if(verbose)
			return GetLongRepresentation();
		else
			return GetShortRepresentation();
	}

	//--------

	public ModelInfo<EModelType> GetInfo()
	{
		return info;
	}

	@Override
	public final boolean equals(Object object)
	{
		if(!(object instanceof ModelEntity))
			return false;

		ModelEntity cmp = ((ModelEntity)object);

		return info.equals(cmp.info);
	}
}

