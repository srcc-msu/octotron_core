/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl.attribute;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.impl.*;
import ru.parallel.octotron.core.logic.Marker;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IMetaAttribute;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.impl.meta.*;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.utils.JavaUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractVaryingAttribute<T extends AttributeObject> extends ModelAttribute implements IMetaAttribute
{
	protected final T meta;

	public AbstractVaryingAttribute(ModelEntity parent, GraphAttribute attribute, T meta)
	{
		super(parent, attribute);
		this.meta = meta;
	}

	public T GetMeta()
	{
		return meta;
	}

	@Override
	public HistoryObject GetLastValue()
	{
		return meta.GetLast();
	}

	@Override
	public long GetCTime()
	{
		return meta.GetCTime();
	}

	@Override
	public long GetATime()
	{
		return meta.GetATime();
	}

	@Override
	public double GetSpeed()
	{
// check time
		HistoryObject last = meta.GetLast();

		if(last == null)
			return 0.0;

		long cur_ctime = GetCTime();
		long last_ctime = last.GetCTime();

		if(cur_ctime - last_ctime == 0) // speed is zero
			return 0.0;

		if(last_ctime == 0) // last value was default
			return 0.0;

		double diff = ToDouble() - last.GetValue().ToDouble();

		return diff / (cur_ctime - last_ctime);
	}

	protected boolean InnerUpdate(Object new_value)
	{
		boolean result = ne(new_value);

		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		for(ReactionObject reaction_object : reaction_objects)
			reaction_object.Repeat(new_value);

		meta.SetLast(GetValue(), GetCTime());

		SetValue(new_value);

		meta.SetCurrent(new_value, JavaUtils.GetTimestamp());

		return result;
	}

	private List<ReactionObject> FilterSuppressed(List<ReactionObject> reaction_objects)
	{
		List<ReactionObject> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
		{
			boolean suppress = false;
			for(Marker marker : reaction_object.GetMarkers())
			{
				if(marker.IsSuppress())
				{
					suppress = true;
					break;
				}
			}

			if(!suppress)
				result.add(reaction_object);
		}

		return result;
	}


	@Override
	public List<Response> GetCurrentReactions()
	{
		List<Response> result = new LinkedList<>();
		List<ReactionObject> reaction_objects = GetReactions();
		reaction_objects = FilterSuppressed(reaction_objects);

		for(ReactionObject reaction_object : reaction_objects)
		{
			if(reaction_object.GetState() == Reaction.STATE_EXECUTED)
				result.add(reaction_object.GetReaction().GetResponse());
		}

		return result;
	}

	public List<Response> ProcessReactions()
	{
		List<Response> result = new LinkedList<>();
		List<ReactionObject> reaction_objects = GetReactions();
		reaction_objects = FilterSuppressed(reaction_objects);

		for(ReactionObject reaction_object : reaction_objects)
		{
			Response response = reaction_object.Process(GetParent());

			if(response != null)
				result.add(response);
		}

		return result;
	}

//-------------------
//      INVALID
//-------------------

	@Override
	public boolean IsValid()
	{
		return meta.GetValid() && GetCTime() != 0;
	}

	@Override
	public void SetValid()
	{
		meta.SetValid(true);
	}

	@Override
	public void SetInvalid()
	{
		meta.SetValid(false);
	}

	private static final SimpleAttribute dependency_link
		= new SimpleAttribute("type", "_depends");

	@Override
	public void AddDependant(VaryingAttribute attribute)
	{
		GraphLink link = GraphService.Get().AddLink(meta.GetBaseObject()
			, attribute.GetMeta().GetBaseObject()
			, dependency_link);

		link.DeclareAttribute(dependency_link);
	}

	@Override
	public AttributeList<VaryingAttribute> GetDependant()
	{
		AttributeList<VaryingAttribute> result = new AttributeList<>();

		for(GraphObject object : meta.GetBaseObject().GetOutNeighbors(dependency_link).Uniq())
		{
			VaryingObject attribute_object = new VaryingObject(object); // only rules will be here
			GraphEntity parent = attribute_object.GetParent();
			GraphAttribute parent_attribute = parent.GetAttribute(attribute_object.GetName());

			result.add(new VaryingAttribute(
				ModelEntity.FromGraph(parent)
				, parent_attribute
				, attribute_object));
		}

		return result;
	}

	@Override
	public void AddReaction(Reaction reaction)
	{
		ReactionObjectFactory.INSTANCE.Create(meta.GetBaseEntity(), reaction);
	}

	@Override
	public List<ReactionObject> GetReactions()
	{
		return ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());
	}

	@Override
	public long AddMarker(Reaction reaction, String description, boolean suppress)
	{
		ReactionObject reaction_object = ReactionObjectFactory
			.INSTANCE.Create(meta.GetBaseEntity(), reaction);

		return reaction_object.AddMarker(description, suppress);
	}

	@Override
	public List<Marker> GetMarkers()
	{
		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		List<Marker> result = new LinkedList<>();

		for(ReactionObject reaction_object : reaction_objects)
			result.addAll(reaction_object.GetMarkers());

		return result;
	}

	@Override
	public void DeleteMarker(long id)
	{
		List<ReactionObject> reaction_objects = ReactionObjectFactory
			.INSTANCE.ObtainAll(meta.GetBaseEntity());

		for(ReactionObject reaction_object : reaction_objects)
			reaction_object.TryDeleteMarker(id);
	}
}
