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
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.EEventStatus;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.*;

public abstract class ModelEntity extends UniqueID<EEntityType>
{
	final Map<String, IModelAttribute> attributes_map = new HashMap<>();

	final Map<String, ConstAttribute> const_map = new HashMap<>();
	final Map<String, SensorAttribute> sensor_map = new HashMap<>();
	final Map<String, VarAttribute> var_map = new HashMap<>();

	public ModelEntity(EEntityType type)
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
		IAttribute result = attributes_map.get(name);

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
}

