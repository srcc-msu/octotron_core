package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.attributes.ConstAttribute;
import ru.parallel.octotron.core.attributes.SensorAttribute;
import ru.parallel.octotron.core.attributes.VarAttribute;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.ReactionTemplate;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.UniqueID;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelEntity extends UniqueID<EEntityType>
{
	public abstract static class ModelEntityBuilder<T extends ModelEntity>
	{
		protected final T entity;

		ModelEntityBuilder(T entity)
		{
			if(ModelService.Get().GetMode() != ModelService.EMode.CREATION)
				throw new ExceptionModelFail("objects creation is not allowed in operational mode");

			this.entity = entity;
		}

		public void AddReaction(ReactionTemplate reaction)
		{
			entity.GetAttribute(reaction.GetCheckName())
				.GetBuilder().AddReaction(new Reaction(reaction, entity));
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

		public void DeclareSensor(String name, Object value)
		{
			if(entity.TestAttribute(name))
				throw new ExceptionModelFail("attribute already declared: " + name);

			SensorAttribute sensor = new SensorAttribute(entity, name, SimpleAttribute.ConformType(value));

			entity.attributes_map.put(name, sensor);
			entity.sensor_map.put(name, sensor);
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

		public void DeclareVar(Rule rule)
		{
			String name = rule.GetName();

			if(entity.TestAttribute(name))
				throw new ExceptionModelFail("attribute already declared: " + name);

			VarAttribute var = new VarAttribute(entity, name, rule);

			entity.attributes_map.put(name, var);
			entity.var_map.put(name, var);
		}

		public void DeclareVar(Iterable<Rule> rules)
		{
			for(Rule rule : rules)
				DeclareVar(rule);
		}
	}

	public abstract ModelEntityBuilder GetBuilder();

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

