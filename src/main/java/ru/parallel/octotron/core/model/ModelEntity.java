package ru.parallel.octotron.core.model;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.model.attribute.*;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.neo4j.impl.Marker;
import sun.management.Sensor;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased implements IEntity
{
	public ModelEntity(GraphService graph_service, GraphEntity base)
	{
		super(graph_service, base);
	}

	public abstract void AddRules(List<OctoRule> rules);

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
		{
			if(!TestAttribute(reaction.GetCheckName()))
				throw new ExceptionModelFail("could not assign a reaction, attribute is missing: " + reaction.GetCheckName());

			ModelAttribute attribute = GetAttribute(reaction.GetCheckName());

			if(attribute.GetType() == EAttributeType.CONSTANT)
				throw new ExceptionModelFail("could not assign a reaction to constant attribute: " + reaction.GetCheckName());

			AttributeObject object
				= this.GetAttributeObject(reaction.GetCheckName());

			object.AddReaction(reaction);
		}
	}

	@Nullable
	public abstract AttributeObject GetAttributeObject(String name);

	public ModelAttribute GetAttribute(String name)
	{
		AttributeObject object = GetAttributeObject(name);

		if(object == null)
			return new ConstantAttribute(this, name);

		else if(object.GetBaseObject().TestLabel(EObjectLabels.SENSOR.toString()))
			return new SensorAttribute(this, name);

		else if(object.GetBaseObject().TestLabel(EObjectLabels.DERIVED.toString()))
			return new DerivedAttribute(this, name);

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

	public void AddRule(OctoRule rule)
	{
	}

	public SensorAttribute GetSensor(String name)
	{
		return new SensorAttribute(this, name);
	}

	public DerivedAttribute GetDerived(String name)
	{
		return new DerivedAttribute(this, name);
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

	public abstract void AddSensor(SimpleAttribute attribute);
}
