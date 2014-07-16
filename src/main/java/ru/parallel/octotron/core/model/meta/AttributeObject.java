package ru.parallel.octotron.core.model.meta;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public abstract class AttributeObject extends MetaObject
{
	protected AttributeObject(GraphService graph_service, GraphObject base)
	{
		super(graph_service, base);
	}

	static final String name_const = "_name";
	static final String value_const = "_value";
	static final String atime_const = "_atime";
	static final String ctime_const = "_ctime";
	static final String valid_const = "_valid";

	public ModelObject GetAttributeObject()
	{
		GraphObject parent = GetBaseObject().GetInNeighbors("_attribute", GetName()).Only();
		return new ModelObject(GetGraphService(), parent);
	}

	public static void Init(GraphObject object, String name, Object value)
	{
		object.DeclareAttribute(name_const, name);
		object.DeclareAttribute(value_const, value);
		object.DeclareAttribute(atime_const, 0L);
		object.DeclareAttribute(ctime_const, 0L);
		object.DeclareAttribute(valid_const, true);
	}

	public String GetName()
	{
		return GetAttribute(name_const).GetString();
	}

	public ModelAttribute GetAttribute()
	{
		return GetAttributeObject().GetAttribute(GetName());
	}

	public boolean GetValid()
	{
		return GetAttribute(valid_const).GetBoolean();
	}

	public void SetValid(boolean value)
	{
		GetBaseObject().UpdateAttribute(valid_const, value);
	}

	public long GetATime()
	{
		return GetAttribute(atime_const).GetLong();
	}

	public long GetCTime()
	{
		return GetAttribute(ctime_const).GetLong();
	}

	public void SetCurrent(Object new_value, long cur_time)
	{
		GetBaseObject().UpdateAttribute(value_const, new_value);
		GetBaseObject().UpdateAttribute(ctime_const, cur_time);
	}

	public void Touch(long cur_time)
	{
		GetBaseObject().UpdateAttribute(atime_const, cur_time);
	}

// REACTIONS
	public List<OctoReaction> GetReactions()
	{
		List<ReactionObject> objects
			= new ReactionObjectFactory().ObtainAll(GetGraphService(), GetBaseObject());

		List<OctoReaction> reactions = new LinkedList<>();

		for(ReactionObject object : objects)
			reactions.add(object.GetReaction());

		return reactions;
	}

	public void AddReaction(OctoReaction reaction)
	{
		new ReactionObjectFactory().Create(GetGraphService(), GetBaseObject(), reaction);
	}

	public long GetReactionState(OctoReaction reaction)
	{
		List<ReactionObject> reactions
			= new ReactionObjectFactory().ObtainAll(GetGraphService(), GetBaseObject(), reaction.GetCheckName());

		for(ReactionObject r : reactions)
		{
			if(r.GetID() == reaction.GetID())
				return r.GetState();
		}
		throw new ExceptionModelFail("reaction not found");
	}

	public void SetReactionState(OctoReaction reaction, long res)
	{
		List<ReactionObject> reactions
			= new ReactionObjectFactory().ObtainAll(GetGraphService(), GetBaseObject(), reaction.GetCheckName());

		for(ReactionObject r : reactions)
		{
			if(r.GetID() == reaction.GetID())
			{
				r.SetState(res);
				return;
			}
		}
		throw new ExceptionModelFail("reaction not found");
	}

// MARKERS

	public List<Marker> GetMarkers()
	{
		List<MarkerObject> objects
			= new MarkerObjectFactory().ObtainAll(GetGraphService(), GetBaseObject());

		List<Marker> markers = new LinkedList<>();

		for(MarkerObject object : objects)
			markers.add(object.GetMarker());

		return markers;
	}

	public void AddMarker(Marker marker)
	{
		new MarkerObjectFactory().Create(GetGraphService(), GetBaseObject(), marker);
	}

	public void DeleteMarker(long marker_id)
	{
		throw new ExceptionModelFail("NIY");
	}

	@Nullable
	public HistoryObject GetLast()
	{
		List<HistoryObject> objects = new HistoryObjectFactory().ObtainAll(GetGraphService(), GetBaseObject());

		if(objects.size() == 0)
			return null;
		else if(objects.size() == 1)
			return objects.get(0);
		else
			throw new ExceptionModelFail("NIY");
	}

	public void SetLast(Object value, long ctime)
	{
		List<HistoryObject> objects = new HistoryObjectFactory().ObtainAll(GetGraphService(), GetBaseObject());

		if(objects.size() == 0)
			new HistoryObjectFactory().Create(GetGraphService(), GetBaseObject(), new HistoryObject.OldPair(value, ctime));
		else if(objects.size() == 1)
			objects.get(0).Set(value, ctime);

	}
}
