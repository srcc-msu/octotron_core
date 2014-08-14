package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VariableAttribute;
import ru.parallel.octotron.core.model.impl.meta.SensorObject;
import ru.parallel.octotron.core.model.impl.meta.SensorObjectFactory;
import ru.parallel.octotron.core.model.impl.meta.VariableObject;
import ru.parallel.octotron.core.model.impl.meta.VariableObjectFactory;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.rule.OctoReaction;
import ru.parallel.octotron.core.rule.OctoResponse;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased implements IEntity<ModelAttribute>
{
	public ModelEntity(GraphEntity base)
	{
		super(base);
	}

// ---------------

	public SensorAttribute DeclareSensor(String name, Object value)
	{
		return DeclareSensor(new SimpleAttribute(name, value));
	}

	public SensorAttribute DeclareSensor(SimpleAttribute attribute)
	{
		GetBaseEntity().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObject meta = new SensorObjectFactory().Create(GetBaseEntity(), attribute);
		return new SensorAttribute(this, meta, attribute.GetName());
	}

	public SensorAttribute GetSensor(String name)
	{
		return new SensorAttribute(this
			, new SensorObjectFactory().Obtain(GetBaseEntity(), name), name);
	}

	public void DeclareSensors(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareSensor(attribute);
	}

// ---------------

	public ConstantAttribute DeclareConstant(SimpleAttribute attribute)
	{
		return DeclareConstant(attribute.GetName(), attribute.GetValue());
	}

	public ConstantAttribute DeclareConstant(String name, Object value)
	{
		GetBaseEntity().DeclareAttribute(name, value);
		return new ConstantAttribute(this, name);
	}

	public ConstantAttribute GetConstant(String name)
	{
		return new ConstantAttribute(this, name);
	}

	public void DeclareConstants(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConstant(attribute);
	}

// ---------------

	public VariableAttribute DeclareVariable(OctoRule rule)
	{
		GetBaseEntity().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
		VariableObject meta = new VariableObjectFactory().Create(GetBaseEntity(), rule);

		return new VariableAttribute(this, meta, rule.GetName());
	}

	public void DeclareVariables(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
		{
			DeclareVariable(rule);
		}
	}

	public VariableAttribute GetVariable(String name)
	{
		return new VariableAttribute(this
			, new VariableObjectFactory().Obtain(GetBaseEntity(), name), name);
	}

// -----------------------------

	@Override
	public ModelAttribute GetAttribute(String name)
	{
		VariableObject derived_object
			= new VariableObjectFactory().TryObtain(GetBaseEntity(), name);

		if(derived_object != null)
			return new VariableAttribute(this, derived_object, name);

		SensorObject sensor_object
			= new SensorObjectFactory().TryObtain(GetBaseEntity(), name);

		if(sensor_object != null)
			return new SensorAttribute(this, sensor_object, name);

		if(GetBaseEntity().TestAttribute(name))
			return new ConstantAttribute(this, name);

		throw new ExceptionModelFail("attribute not found: " + name);
	}

	@Override
	public AttributeList<ModelAttribute> GetAttributes()
	{
		AttributeList<ModelAttribute> attributes = new AttributeList<>();

		for(GraphAttribute attribute : GetBaseEntity().GetAttributes())
		{
			attributes.add(GetAttribute(attribute.GetName()));
		}

		return attributes;
	}

	@Override
	public boolean TestAttribute(String name)
	{
		return GetBaseEntity().TestAttribute(name);
	}


	@Override
	public boolean TestAttribute(String name, Object value)
	{
		return GetBaseEntity().TestAttribute(name, value);
	}


	@Override
	public boolean TestAttribute(SimpleAttribute attribute)
	{
		return GetBaseEntity().TestAttribute(attribute);
	}

// -----------

	public long AddMarker(OctoReaction reaction, String description, boolean suppress)
	{
		return GetAttribute(reaction.GetCheckName())
			.AddMarker(reaction, description, suppress);
	}

	public void DeleteMarker(String name, long id)
	{
		GetAttribute(name).DeleteMarker(id);
	}

	public List<Marker> GetMarkers()
	{
		List<Marker> result = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.addAll(attribute.GetMarkers());

		return result;
	}

// -----------

	public void AddReaction(OctoReaction reaction)
	{
		if(!TestAttribute(reaction.GetCheckName()))
			throw new ExceptionModelFail("could not assign a reaction, attribute is missing: " + reaction.GetCheckName());

		GetAttribute(reaction.GetCheckName())
			.AddReaction(reaction);
	}

	public List<OctoReaction> GetReactions()
	{
		List<OctoReaction> result = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.addAll(attribute.GetReactions());

		return result;
	}

	public List<OctoReaction> GetReactions(String name)
	{
		List<OctoReaction> result = new LinkedList<>();

		result.addAll(GetAttribute(name).GetReactions());

		return result;
	}

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
		{
			AddReaction(reaction);
		}
	}

// --------------

	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.GetExecutedReactions());
		}

		return responses;
	}

	public List<OctoResponse> PreparePendingReactions()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.GetReadyReactions());
		}

		return responses;
	}
}
