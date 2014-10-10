package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ModelEntity extends UniqueID<EEntityType>
{
	final Map<String, IModelAttribute> attributes_map;

	final Map<String, ConstAttribute> const_map;
	final Map<String, SensorAttribute> sensor_map;
	final Map<String, VarAttribute> var_map;

	public ModelEntity(EEntityType type)
	{
		super(type);

		attributes_map = new HashMap<>();
		const_map = new HashMap<>();
		sensor_map = new HashMap<>();
		var_map = new HashMap<>();
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
}

