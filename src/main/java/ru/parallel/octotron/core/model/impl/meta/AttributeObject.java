package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.OctoReaction;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public abstract class AttributeObject extends MetaObject
{
	protected AttributeObject(GraphObject base)
	{
		super(base);
	}

	static final String name_const = "_name";
	static final String value_const = "_value";
	static final String atime_const = "_atime";
	static final String ctime_const = "_ctime";
	static final String valid_const = "_valid";

	public ModelObject GetAttributeObject()
	{
		GraphObject parent = GetBaseObject().GetInNeighbors("_meta", GetName()).Only();
		return new ModelObject(parent);
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

	public ModelAttribute GetParentAttribute()
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
			= new ReactionObjectFactory().ObtainAll(GetBaseObject());

		List<OctoReaction> reactions = new LinkedList<>();

		for(ReactionObject object : objects)
			reactions.add(object.GetReaction());

		return reactions;
	}

	@Nullable
	public HistoryObject GetLast()
	{
		List<HistoryObject> objects = new HistoryObjectFactory().ObtainAll(GetBaseObject());

		if(objects.size() == 0)
			return null;
		else if(objects.size() == 1)
			return objects.get(0);
		else
			throw new ExceptionModelFail("NIY");
	}

	public void SetLast(Object value, long ctime)
	{
		List<HistoryObject> objects = new HistoryObjectFactory().ObtainAll(GetBaseObject());

		if(objects.size() == 0)
			new HistoryObjectFactory().Create(GetBaseObject(), new HistoryObject.OldPair(value, ctime));
		else if(objects.size() == 1)
			objects.get(0).Set(value, ctime);
		else
			throw new ExceptionModelFail("NIY");
	}
}
