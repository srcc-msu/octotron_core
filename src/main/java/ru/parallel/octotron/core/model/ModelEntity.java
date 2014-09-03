package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.OctoResponse;
import ru.parallel.octotron.core.OctoRule;
import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.SensorObject;
import ru.parallel.octotron.core.model.impl.meta.SensorObjectFactory;
import ru.parallel.octotron.core.model.impl.meta.VaryingObject;
import ru.parallel.octotron.core.model.impl.meta.VaryingObjectFactory;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
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
		GraphAttribute graph_attribute = GetBaseEntity().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObject meta = SensorObjectFactory.INSTANCE.Create(GetBaseEntity(), attribute);
		return new SensorAttribute(this, graph_attribute, meta);
	}

	public SensorAttribute GetSensor(String name)
	{
		return GetAttribute(name).ToSensor();
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
		GraphAttribute graph_attribute = GetBaseEntity().DeclareAttribute(name, value);
		return new ConstantAttribute(this, graph_attribute);
	}

	public ConstantAttribute GetConstant(String name)
	{
		return GetAttribute(name).ToConstant();
	}

	public void DeclareConstants(List<SimpleAttribute> attributes)
	{
		for(SimpleAttribute attribute : attributes)
			DeclareConstant(attribute);
	}

// ---------------

	public VaryingAttribute DeclareVarying(OctoRule rule)
	{
		GraphAttribute graph_attribute = GetBaseEntity().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
		VaryingObject meta = VaryingObjectFactory.INSTANCE.Create(GetBaseEntity(), rule);

		return new VaryingAttribute(this, graph_attribute, meta);
	}

	public void DeclareVaryings(List<OctoRule> rules)
	{
		for(OctoRule rule : rules)
		{
			DeclareVarying(rule);
		}
	}

	public VaryingAttribute GetVarying(String name)
	{
		return GetAttribute(name).ToVarying();
	}

// -----------------------------

	public ModelAttribute GetAttribute(String name)
	{
		return new ModelAttribute(this, GetBaseEntity().GetAttribute(name));
	}

	@Override
	public AttributeList<ModelAttribute> GetAttributes()
	{
		AttributeList<ModelAttribute> attributes = new AttributeList<>();

		for(GraphAttribute attribute : GetBaseEntity().GetAttributes())
		{
			attributes.add(new ModelAttribute(this, attribute));
		}

		return attributes;
	}

	public AttributeList<ConstantAttribute> GetConstants()
	{
		AttributeList<ConstantAttribute> attributes = new AttributeList<>();
		throw new ExceptionModelFail("NIY");
	}

	public AttributeList<SensorAttribute> GetSensors()
	{
		AttributeList<SensorAttribute> attributes = new AttributeList<>();
		throw new ExceptionModelFail("NIY");
	}

	public AttributeList<VaryingAttribute> GetVaryings()
	{
		AttributeList<VaryingAttribute> attributes = new AttributeList<>();
		throw new ExceptionModelFail("NIY");
	}

	public IMetaAttribute GetMetaAttribute(String name)
	{
		return GetAttribute(name).ToMeta();
	}

	public AttributeList<IMetaAttribute> GetMetaAttributes()
	{
		AttributeList<IMetaAttribute> result = new AttributeList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.add(attribute.ToMeta());

		return result;
	}

// -----------------------------

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
		return GetAttribute(reaction.GetCheckName()).ToMeta()
			.AddMarker(reaction, description, suppress);
	}

	public void DeleteMarker(String name, long id)
	{
		GetAttribute(name).ToMeta().DeleteMarker(id);
	}

	public List<Marker> GetMarkers()
	{
		List<Marker> result = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.addAll(attribute.ToMeta().GetMarkers());

		return result;
	}

// -----------

	public void AddReaction(OctoReaction reaction)
	{
		if(!TestAttribute(reaction.GetCheckName()))
			throw new ExceptionModelFail("could not assign a reaction, attribute is missing: " + reaction.GetCheckName());

		GetAttribute(reaction.GetCheckName()).ToMeta()
			.AddReaction(reaction);
	}

	public List<OctoReaction> GetReactions()
	{
		List<OctoReaction> result = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.addAll(attribute.ToMeta().GetReactions());

		return result;
	}

	public List<OctoReaction> GetReactions(String name)
	{
		List<OctoReaction> result = new LinkedList<>();

		result.addAll(GetAttribute(name).ToMeta().GetReactions());

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
			responses.addAll(attribute.ToMeta().GetExecutedReactions());
		}

		return responses;
	}

	public List<OctoResponse> PreparePendingReactions()
	{
		List<OctoResponse> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.ToMeta().GetReadyReactions());
		}

		return responses;
	}
}
