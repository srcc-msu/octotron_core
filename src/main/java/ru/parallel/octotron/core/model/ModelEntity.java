package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.GraphAttribute;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.*;
import ru.parallel.octotron.core.primitive.EEntityType;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public abstract class ModelEntity extends GraphBased implements IEntity<ModelAttribute>
{
	public ModelEntity(GraphEntity base)
	{
		super(base);
	}

	public static ModelEntity FromGraph(GraphEntity entity)
	{
		if(entity.GetUID().getType() == EEntityType.OBJECT)
			return new ModelObject((GraphObject)entity);
		else if(entity.GetUID().getType() == EEntityType.OBJECT)
			return new ModelObject((GraphObject)entity);
		else
			throw new ExceptionModelFail("WTF");
	}

// ---------------

	public void DeclareSensor(String name, Object value)
	{
		DeclareSensor(new SimpleAttribute(name, value));
	}

	public void DeclareSensor(SimpleAttribute attribute)
	{
		GetBaseEntity().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObjectFactory.INSTANCE.Create(GetBaseEntity(), attribute);
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

	public void DeclareConstant(SimpleAttribute attribute)
	{
		DeclareConstant(attribute.GetName(), attribute.GetValue());
	}

	public void DeclareConstant(String name, Object value)
	{
		GetBaseEntity().DeclareAttribute(name, value);
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

	public void DeclareVarying(Rule rule)
	{
		GetBaseEntity().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
		VaryingObjectFactory.INSTANCE.Create(GetBaseEntity(), rule);
	}

	public void DeclareVaryings(List<Rule> rules)
	{
		for(Rule rule : rules)
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

		for(ModelAttribute attribute : GetAttributes())
		{
			boolean t1 = SensorObjectFactory.INSTANCE.Test(GetBaseEntity(), attribute.GetName());
			boolean t2 = VaryingObjectFactory.INSTANCE.Test(GetBaseEntity(), attribute.GetName());

			if(t1 || t2)
				continue;

			attributes.add(attribute.ToConstant());
		}

		return attributes;
	}

	public AttributeList<SensorAttribute> GetSensors()
	{
		AttributeList<SensorAttribute> attributes = new AttributeList<>();

		for(SensorObject object : SensorObjectFactory.INSTANCE.ObtainAll(GetBaseEntity()))
			attributes.add(new SensorAttribute(this, GetBaseEntity().GetAttribute(object.GetName()), object));

		return attributes;
	}

	public AttributeList<VaryingAttribute> GetVaryings()
	{
		AttributeList<VaryingAttribute> attributes = new AttributeList<>();

		for(VaryingObject object : VaryingObjectFactory.INSTANCE.ObtainAll(GetBaseEntity()))
			attributes.add(new VaryingAttribute(this, GetBaseEntity().GetAttribute(object.GetName()), object));

		return attributes;
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

	public long AddMarker(Reaction reaction, String description, boolean suppress)
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

	public void AddReaction(Reaction reaction)
	{
		if(!TestAttribute(reaction.GetCheckName()))
			throw new ExceptionModelFail("could not assign a reaction, attribute is missing: " + reaction.GetCheckName());

		GetAttribute(reaction.GetCheckName()).ToMeta()
			.AddReaction(reaction);
	}

	public List<ReactionObject> GetReactions()
	{
		List<ReactionObject> result = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
			result.addAll(attribute.ToMeta().GetReactions());

		return result;
	}

	public List<ReactionObject> GetReactions(String name)
	{
		List<ReactionObject> result = new LinkedList<>();

		result.addAll(GetAttribute(name).ToMeta().GetReactions());

		return result;
	}

	public void AddReactions(List<Reaction> reactions)
	{
		for(Reaction reaction : reactions)
		{
			AddReaction(reaction);
		}
	}

// --------------

	public List<Response> ProcessReactions()
	{
		List<Response> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.ToMeta().ProcessReactions());
		}

		return responses;
	}

	public List<Response> GetCurrentReactions()
	{
		List<Response> responses = new LinkedList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			responses.addAll(attribute.ToMeta().GetCurrentReactions());
		}

		return responses;
	}
}
