package ru.parallel.octotron.core.model.impl.meta;

import ru.parallel.octotron.core.graph.impl.GraphEntity;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public abstract class AttributeObject extends MetaObject
{
	protected AttributeObject(GraphObject base)
	{
		super(base);
	}

	static final String ctime_const = "_ctime";
	static final String valid_const = "_valid";

	public static void Init(GraphObject object)
	{
		object.DeclareAttribute(ctime_const, 0L);
		object.DeclareAttribute(valid_const, true);
	}

	public GraphObject GetParent()
	{
		return MetaObjectFactory.GetParent(GetBaseObject());
	}

	public String GetName()
	{
		return MetaObjectFactory.GetName(GetBaseObject());
	}

	public boolean GetValid()
	{
		return GetAttribute(valid_const).GetBoolean();
	}

	public void SetValid(boolean value)
	{
		GetBaseObject().UpdateAttribute(valid_const, value);
	}

	public long GetCTime()
	{
		return GetAttribute(ctime_const).GetLong();
	}

	public void SetCurrent(long cur_time)
	{
		GetBaseObject().UpdateAttribute(ctime_const, cur_time);
	}

// REACTIONS
	public List<Reaction> GetReactions()
	{
		List<ReactionObject> objects
			= ReactionObjectFactory.INSTANCE.ObtainAll(GetBaseObject());

		List<Reaction> reactions = new LinkedList<>();

		for(ReactionObject object : objects)
			reactions.add(object.GetReaction());

		return reactions;
	}

	@Nullable
	public HistoryObject GetLast()
	{
		List<HistoryObject> objects = HistoryObjectFactory.INSTANCE.ObtainAll(GetBaseObject());

		if(objects.size() == 0)
			return null;
		else if(objects.size() == 1)
			return objects.get(0);
		else
			throw new ExceptionModelFail("NIY");
	}

	public void SetLast(Object value, long ctime)
	{
		List<HistoryObject> objects = HistoryObjectFactory.INSTANCE.ObtainAll(GetBaseObject());

		if(objects.size() == 0)
			HistoryObjectFactory.INSTANCE.Create(GetBaseObject(), new HistoryObject.OldPair(value, ctime));
		else if(objects.size() == 1)
			objects.get(0).Set(value, ctime);
		else
			throw new ExceptionModelFail("NIY");
	}
}
