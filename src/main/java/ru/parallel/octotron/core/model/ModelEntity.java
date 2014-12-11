/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.EModelType;
import ru.parallel.octotron.core.primitive.IPresentable;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.exec.services.ModelService;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.*;

public abstract class ModelEntity extends ModelID<EModelType> implements IPresentable
{
	final Map<String, IModelAttribute> attributes_map = new HashMap<>();

	final Map<String, ConstAttribute> const_map = new HashMap<>();
	final Map<String, SensorAttribute> sensor_map = new HashMap<>();
	final Map<String, VarAttribute> var_map = new HashMap<>();

	public ModelEntity(EModelType type)
	{
		super(type);
	}

	public abstract ModelEntityBuilder<?> GetBuilder(ModelService service);

	public IModelAttribute GetAttribute(String name)
	{
		IModelAttribute result;

		result = const_map.get(name);
		if(result != null)
			return result;

		result = sensor_map.get(name);
		if(result != null)
			return result;

		result = var_map.get(name);
		if(result != null)
			return result;

		throw new ExceptionModelFail("attribute not found: " + name);
	}

	public Collection<IModelAttribute> GetAttributes()
	{
		return attributes_map.values();
	}

	public Collection<? extends IModelAttribute> GetAttributes(EAttributeType type)
	{
		switch(type)
		{
			case CONST:
				return const_map.values();
			case SENSOR:
				return sensor_map.values();
			case VAR:
				return var_map.values();
			default:
				throw new ExceptionModelFail("unknown attribute type: " + type.toString());
		}
	}


	public Map<String, Object> GetAttributesValues()
	{
		Map<String, Object> result = new HashMap<>();

		for(IModelAttribute attribute : GetAttributes())
		{
			result.put(attribute.GetName(), attribute.GetValue());
		}

		return result;
	}

	public boolean TestAttribute(String name)
	{
		IModelAttribute result = attributes_map.get(name);

		return result != null;
	}

// ----------------

	public ConstAttribute GetConst(String name)
	{
		return const_map.get(name);
	}

	public Collection<ConstAttribute> GetConst()
	{
		return const_map.values();
	}

// ----------------

	public SensorAttribute GetSensor(String name)
	{
		return sensor_map.get(name);
	}

	public Collection<SensorAttribute> GetSensor()
	{
		return sensor_map.values();
	}

// ----------------

	public VarAttribute GetVar(String name)
	{
		return var_map.get(name);
	}

	public Collection<VarAttribute> GetVar()
	{
		return var_map.values();
	}

	public Collection<PreparedResponse> GetPreparedResponses()
	{
		List<PreparedResponse> result = new LinkedList<>();

		for(IModelAttribute attribute : GetAttributes())
		{
			for(Reaction reaction : attribute.GetReactions())
			{
				PreparedResponse prepared_response = reaction.GetPreparedResponse();

				if(prepared_response != null && prepared_response.GetResponse().GetStatus() != EEventStatus.RECOVER)
					result.add(prepared_response);
			}
		}

		return result;
	}

	@Override
	public Map<String, Object> GetShortRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetID());

		List<Map<String, Object>> const_list = new LinkedList<>();

		for(ConstAttribute attribute : GetConst())
		{
			const_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> sensor_list = new LinkedList<>();
		for(SensorAttribute attribute : GetSensor())
		{
			sensor_list.add(attribute.GetShortRepresentation());
		}

		List<Map<String, Object>> var_list = new LinkedList<>();
		for(VarAttribute attribute : GetVar())
		{
			var_list.add(attribute.GetShortRepresentation());
		}

		result.put("const", const_list);
		result.put("sensor", sensor_list);
		result.put("var", var_list);

		return result;
	}

	@Override
	public Map<String, Object> GetLongRepresentation()
	{
		Map<String, Object> result = new HashMap<>();
		result.put("AID", GetID());

		List<Map<String, Object>> const_list = new LinkedList<>();

		for(ConstAttribute attribute : GetConst())
		{
			const_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> sensor_list = new LinkedList<>();
		for(SensorAttribute attribute : GetSensor())
		{
			sensor_list.add(attribute.GetLongRepresentation());
		}

		List<Map<String, Object>> var_list = new LinkedList<>();
		for(VarAttribute attribute : GetVar())
		{
			var_list.add(attribute.GetLongRepresentation());
		}

		result.put("const", const_list);
		result.put("sensor", sensor_list);
		result.put("var", var_list);

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
}

