package ru.parallel.octotron.core.model.attribute;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.collections.ListConverter;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphBased;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelAttribute;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EObjectLabels;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.impl.PersistentStorage;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public abstract class AttributeObject extends GraphBased
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

// LAST
	private static final SimpleAttribute last_type
		= new SimpleAttribute("type", "_last");

	public ObjectList<GraphObject, GraphLink> GetLastObject()
	{
		return ListConverter.FilterLabel(GetBaseObject().GetOutNeighbors(), EObjectLabels.LAST.toString());
	}

	public void SetLast(Object value, long ctime)
	{
		ObjectList<GraphObject, GraphLink> list = GetLastObject();

		if(list.size() == 0)
		{
			GraphObject last = GetGraphService().AddObject();
			GetGraphService().AddLink(GetBaseObject(), last, last_type);
		}

		GraphObject object = list.Only();

		object.UpdateAttribute(value_const, value);
		object.UpdateAttribute(ctime_const, ctime);
	}

	@Nullable
	public GraphObject GetLast()
	{
		ObjectList<GraphObject, GraphLink> list = GetLastObject();

		if(list.size() == 0)
			return null;

		return list.Only();
	}

// REACTIONS

	private static final SimpleAttribute reaction_type
		= new SimpleAttribute("type", "_reaction");

	private static final String reaction_id_const = "_id";
	private static final String reaction_status_const = "_status";

	public List<OctoReaction> GetReactions()
	{
		ObjectList<GraphObject, GraphLink> nearest = GetReactionsObjects();

		List<OctoReaction> reactions = new LinkedList<>();

		for(GraphObject object : nearest)
			reactions.add(PersistentStorage.INSTANCE.GetReactions()
				.Get(object.GetAttribute(reaction_id_const).GetLong()));

		return reactions;
	}

	public ObjectList<GraphObject, GraphLink> GetReactionsObjects()
	{
		return ListConverter.FilterLabel(
			GetBaseObject().GetOutNeighbors()
			, EObjectLabels.REACTION.toString());
	}

	public void AddReaction(OctoReaction reaction)
	{
		GraphObject object = GetGraphService().AddObject();
		GetGraphService().AddLink(GetBaseObject(), object, reaction_type);
		object.AddLabel(EObjectLabels.REACTION.toString());

		object.DeclareAttribute(reaction_id_const, reaction.GetID());
		object.DeclareAttribute(reaction_status_const, 0L);
	}

	public long GetReactionState(long key)
	{
		GraphObject object = GetReactionsObjects().Filter(reaction_id_const, key).Only();

		return object.GetAttribute(reaction_status_const).GetLong();
	}

	public void SetReactionState(long key, long res)
	{
		GraphObject object = GetReactionsObjects().Filter(reaction_id_const, key).Only();

		object.UpdateAttribute(reaction_status_const, res);
	}

// MARKERS

	private static final SimpleAttribute mark_type
		= new SimpleAttribute("type", "_mark");

	private static final String mark_id_const = "_mark_id";
	private static final String mark_r_id_const = "_mark_r_id";
	private static final String mark_descr_const = "_mark_descr";
	private static final String mark_suppress_const = "_mark_suppress";

	public List<Marker> GetMarkers()
	{
		ObjectList<GraphObject, GraphLink> nearest = GetMarkersObjects();

		List<Marker> markers = new LinkedList<>();

		for(GraphObject object : nearest)
			markers.add(new Marker(
				object.GetAttribute(mark_id_const).GetLong(),
				object.GetAttribute(mark_r_id_const).GetLong(),
				object.GetAttribute(mark_descr_const).GetString(),
				object.GetAttribute(mark_suppress_const).GetBoolean()));

		return markers;
	}

	public ObjectList<GraphObject, GraphLink> GetMarkersObjects()
	{
		return ListConverter.FilterLabel(
			GetBaseObject().GetOutNeighbors()
			, EObjectLabels.MARK.toString());
	}

	public void AddMarker(long reaction_id, String description, boolean suppress)
	{
		GraphObject object = GetGraphService().AddObject();
		GetGraphService().AddLink(GetBaseObject(), object, mark_type);
		object.AddLabel(EObjectLabels.MARK.toString());

		object.DeclareAttribute(mark_id_const, 0); // TODO
		object.DeclareAttribute(mark_r_id_const, reaction_id);
		object.DeclareAttribute(mark_descr_const, description);
		object.DeclareAttribute(mark_suppress_const, suppress);
	}

	public void DeleteMarker(long marker_id)
	{
		ObjectList<GraphObject, GraphLink> objects = GetMarkersObjects();
		objects.Filter(mark_id_const, marker_id).Only().Delete();
	}

//	public abstract EAttributeType GetType();
}
