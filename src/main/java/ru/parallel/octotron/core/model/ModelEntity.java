package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.attribute.*;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.rule.OctoRule;
import ru.parallel.octotron.neo4j.impl.Marker;
import ru.parallel.octotron.rules.MirrorLong;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased
{
	public ModelEntity(GraphEntity base)
	{
		super(base);
	}

	public abstract void DeclareAttributes(List<SimpleAttribute> attributes);
	public abstract void AddRules(List<OctoRule> rules);
	public abstract void AddReactions(List<OctoReaction> reactions);

	private static final String extended_attr = "_extended_attr";

	public ModelAttribute GetAttribute(String name)
	{
		Sensor sensor = Sensor.TryConstruct(this, name);
		if(sensor != null)
			return sensor;

		Derived derived = Derived.TryConstruct(this, name);
		if(derived != null)
			return derived;

		Constant constant = Constant.TryConstruct(this, name);
		if(constant != null)
			return constant;

		throw new ExceptionModelFail("attribute not found: " + name);
	}

	public List<OctoResponse> GetFails()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(AbstractVaryingAttribute attribute : GetAttributes())
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

		for(AbstractVaryingAttribute attribute : GetAttributes())
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

	public void AddRule(MirrorLong mirrorLong)
	{
	}

	public Sensor GetSensor(String name)
	{
		Sensor sensor = Sensor.TryConstruct(this, name);

		if(sensor == null)
			throw new ExceptionModelFail("sensor not found: " + name);

		return sensor;
	}

	public Derived GetDerived(String name)
	{
		Derived derived = Derived.TryConstruct(this, name);

		if(derived == null)
			throw new ExceptionModelFail("derived not found: " + name);

		return derived;
	}

	public Constant GetConstant(String name)
	{
		Constant constant = Constant.TryConstruct(this, name);

		if(constant == null)
			throw new ExceptionModelFail("constant not found: " + name);

		return constant;
	}

	public static ModelEntity Obtain(GraphEntity entity)
	{
		if(entity.GetUID().getType() == EEntityType.OBJECT)
			return new ModelObject((GraphObject)entity);
		else if(entity.GetUID().getType() == EEntityType.LINK)
			return new ModelLink((GraphLink)entity);
		else
			throw new ExceptionModelFail("unknown entity type");
	}
}
