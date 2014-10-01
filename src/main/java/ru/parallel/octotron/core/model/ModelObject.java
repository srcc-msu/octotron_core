package ru.parallel.octotron.core.model;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.impl.GraphLinkList;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphObjectList;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.logic.Rule;
import ru.parallel.octotron.core.model.collections.ModelLinkList;
import ru.parallel.octotron.core.model.collections.ModelObjectList;
import ru.parallel.octotron.core.model.impl.attribute.ConstantAttribute;
import ru.parallel.octotron.core.model.impl.attribute.SensorAttribute;
import ru.parallel.octotron.core.model.impl.attribute.VaryingAttribute;
import ru.parallel.octotron.core.model.impl.meta.*;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

public class ModelObject extends ModelEntity
{
	public ModelObject(GraphObject object)
	{
		super(object);
	}

	public ModelLinkList GetInLinks()
	{
		GraphLinkList links
			= GetBaseObject().GetInLinks();

		GraphLinkList filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelLinkList GetOutLinks()
	{
		GraphLinkList links
			= GetBaseObject().GetOutLinks();

		GraphLinkList filtered
			= ListConverter.FilterLabel(links, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetInNeighbors()
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors();

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetOutNeighbors()
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors();

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}
	public ModelObjectList GetInNeighbors(String link_name, Object link_value)
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors(link_name, link_value);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors(link_name, link_value);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetInNeighbors(String link_name)
	{
		GraphObjectList Objects
			= GetBaseObject().GetInNeighbors(link_name);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetOutNeighbors(String link_name)
	{
		GraphObjectList Objects
			= GetBaseObject().GetOutNeighbors(link_name);

		GraphObjectList filtered
			= ListConverter.FilterLabel(Objects, EObjectLabels.MODEL.toString());

		return ListConverter.GraphToModel(filtered);
	}

	public ModelObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return GetInNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

	public ModelObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return GetOutNeighbors(link_attribute.GetName(), link_attribute.GetValue());
	}

// ---------------

	public void DeclareSensor(String name, Object value)
	{
		DeclareSensor(new SimpleAttribute(name, value));
	}

	public void DeclareSensor(SimpleAttribute attribute)
	{
		GetBaseObject().DeclareAttribute(attribute.GetName(), attribute.GetValue());
		SensorObjectFactory.INSTANCE.Create(GetBaseObject(), attribute);
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

	public void DeclareVarying(Rule rule)
	{
		GetBaseObject().DeclareAttribute(rule.GetName(), rule.GetDefaultValue());
		VaryingObjectFactory.INSTANCE.Create(GetBaseObject(), rule);
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

	public AttributeList<ConstantAttribute> GetConstants()
	{
		AttributeList<ConstantAttribute> attributes = new AttributeList<>();

		for(ModelAttribute attribute : GetAttributes())
		{
			boolean t1 = SensorObjectFactory.INSTANCE.Test(GetBaseObject(), attribute.GetName());
			boolean t2 = VaryingObjectFactory.INSTANCE.Test(GetBaseObject(), attribute.GetName());

			if(t1 || t2)
				continue;

			attributes.add(attribute.ToConstant());
		}

		return attributes;
	}

	public AttributeList<SensorAttribute> GetSensors()
	{
		AttributeList<SensorAttribute> attributes = new AttributeList<>();

		for(SensorObject object : SensorObjectFactory.INSTANCE.ObtainAll(GetBaseObject()))
			attributes.add(new SensorAttribute(this, GetBaseObject().GetAttribute(object.GetName()), object));

		return attributes;
	}

	public AttributeList<VaryingAttribute> GetVaryings()
	{
		AttributeList<VaryingAttribute> attributes = new AttributeList<>();

		for(VaryingObject object : VaryingObjectFactory.INSTANCE.ObtainAll(GetBaseObject()))
			attributes.add(new VaryingAttribute(this, GetBaseObject().GetAttribute(object.GetName()), object));

		return attributes;
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
