package ru.parallel.octotron.core.model;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.attribute.DerivedAttribute;
import ru.parallel.octotron.core.model.attribute.EAttributeType;
import ru.parallel.octotron.core.model.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.meta.*;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased implements IEntity
{
	public ModelEntity(GraphService graph_service, GraphEntity base)
	{
		super(graph_service, base);
	}

	public ModelAttribute AddSensor(String name, Object value)
	{
		return AddSensor(new SimpleAttribute(name, value));
	}

	public ModelAttribute AddSensor(SimpleAttribute attribute)
	{
		GetBaseEntity().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObject meta = new SensorObjectFactory().Create(GetGraphService(), GetBaseEntity(), attribute);
		return new SensorAttribute(this, meta, attribute.GetName());
	}

	public void AddRule(OctoRule rule)
	{
		GetBaseEntity().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
		new DerivedObjectFactory().Create(GetGraphService(), GetBaseEntity(), rule);
	}

	public void AddReaction(OctoReaction reaction)
	{
		if(!TestAttribute(reaction.GetCheckName()))
			throw new ExceptionModelFail("could not assign a reaction, attribute is missing: " + reaction.GetCheckName());

		ModelAttribute attribute = GetAttribute(reaction.GetCheckName());

		if(attribute.GetType() == EAttributeType.CONSTANT)
			throw new ExceptionModelFail("could not assign a reaction to constant attribute: " + reaction.GetCheckName());

		AttributeObject object
			= this.TryGetAttributeObject(reaction.GetCheckName());

		object.AddReaction(reaction);
	}

	@Nullable
	public AttributeObject TryGetAttributeObject(String name)
	{
		DerivedObject derived_object
			= new DerivedObjectFactory().TryObtain(GetGraphService(), GetBaseEntity(), name);

		if(derived_object != null)
			return derived_object;

		SensorObject sensor_object
			= new SensorObjectFactory().TryObtain(GetGraphService(), GetBaseEntity(), name);

		if(sensor_object != null)
			return sensor_object;

		return null;
	}

	public ModelAttribute GetAttribute(String name)
	{
		DerivedObject derived_object
			= new DerivedObjectFactory().TryObtain(GetGraphService(), GetBaseEntity(), name);

		if(derived_object != null)
			return new DerivedAttribute(this, derived_object, name);

		SensorObject sensor_object
			= new SensorObjectFactory().TryObtain(GetGraphService(), GetBaseEntity(), name);

		if(sensor_object != null)
			return new SensorAttribute(this, sensor_object, name);

		if(GetBaseEntity().TestAttribute(name))
			return new ConstantAttribute(this, name);

		throw new ExceptionModelFail("attribute not found: " + name);
	}

	public AttributeList<ModelAttribute> GetAttributes()
	{
		AttributeList<ModelAttribute> attributes = new AttributeList<>();

		for(GraphAttribute attribute : GetBaseEntity().GetAttributes())
		{
			attributes.add(GetAttribute(attribute.GetName()));
		}

		return attributes;
	}

	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.GetFails());
		}

		return responses;
	}

	public long AddMarker(long reaction_id, String description, boolean suppress)
	{
		return 0;
	}

	public void DeleteMarker(long id)
	{

	}

	public List<OctoResponse> PreparePendingReactions()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.PreparePendingReactions());
		}

		return responses;
	}

	public List<OctoRule> GetRules()
	{
		return null;
	}

	public List<OctoReaction> GetReactions()
	{
		return null;
	}

	public List<Marker> GetMarkers()
	{
		return null;
	}

	public SensorAttribute GetSensor(String name)
	{
		return new SensorAttribute(this
			, new SensorObjectFactory().Obtain(GetGraphService(), GetBaseEntity(), name), name);
	}

	public DerivedAttribute GetDerived(String name)
	{
		return new DerivedAttribute(this
			, new DerivedObjectFactory().Obtain(GetGraphService(), GetBaseEntity(), name), name);
	}

	public ConstantAttribute GetConstant(String name)
	{
		return new ConstantAttribute(this, name);
	}

	public ModelAttribute DeclareConstant(SimpleAttribute attribute)
	{
		return DeclareConstant(attribute.GetName(), attribute.GetValue());
	}

	public ModelAttribute DeclareConstant(String name, Object value)
	{
		GetBaseEntity().DeclareAttribute(name, value);
		return GetAttribute(name);
	}

	public void DeclareConstants(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConstant(attribute);
	}

	public void AddSensors(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			AddSensor(attribute);
	}

	public void AddRules(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
		{
			AddRule(rule);
		}
	}

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
		{
			AddReaction(reaction);
		}
	}
}
