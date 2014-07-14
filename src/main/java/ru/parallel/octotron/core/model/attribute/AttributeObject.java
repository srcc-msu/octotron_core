package ru.parallel.octotron.core.model.attribute;

import com.sun.istack.internal.Nullable;
import ru.parallel.octotron.core.OctoReaction;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.graph.collections.LinkList;
import ru.parallel.octotron.core.graph.collections.ObjectList;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.impl.PersistentStorage;
import ru.parallel.octotron.neo4j.impl.Marker;

import java.util.LinkedList;
import java.util.List;

public class AttributeObject extends GraphBased implements IObject
{
	GraphService graph_service;

	private static final String REACTION_PREFIX = "_reaction";
	private static final String REACTION_EXECUTED_PREFIX = "_reaction_executed";
	private static final String MARKER_PREFIX = "_marker";

	protected AttributeObject(GraphObject base)
	{
		super(base);
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
		return GetOutNeighbors().Filter(reaction_type);
	}

	public void AddReaction(OctoReaction reaction)
	{
		GraphObject object = graph_service.AddObject();

		object.DeclareAttribute(reaction_id_const, reaction.GetID());
		object.DeclareAttribute(reaction_status_const, 0L);
	}

	public void AddReactions(List<OctoReaction> reactions)
	{
		for(OctoReaction reaction : reactions)
			AddReaction(reaction);
	}

	public long GetReactionState(long key)
	{
		GraphObject object = GetReactionsObjects().Filter(reaction_id_const, key).Only();

		return object.GetAttribute(reaction_status_const).GetLong();
	}

	public void SetReactionState(long key, long res)
	{
		GraphObject object = GetReactionsObjects().Filter(reaction_id_const, key).Only();
		graph_service.AddLink(GetBaseObject(), object, reaction_type);

		object.SetAttribute(reaction_status_const, res);
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

	public ObjectList GetMarkersObjects()
	{
		return GetOutNeighbors().Filter(mark_type);
	}

	public void AddMarker(long reaction_id, String description, boolean suppress)
	{
		GraphObject object = graph_service.AddObject();
		graph_service.AddLink(GetBaseObject(), object, mark_type);

		object.DeclareAttribute(mark_id_const, 0); // TODO
		object.DeclareAttribute(mark_r_id_const, reaction_id);
		object.DeclareAttribute(mark_descr_const, description);
		object.DeclareAttribute(mark_suppress_const, suppress);
	}

	public void DeleteMarker(long marker_id)
	{
		ObjectList objects = GetMarkersObjects();
		objects.Filter(mark_id_const, marker_id).Only().Delete();
	}

// LAST

	private static final SimpleAttribute last_type
		= new SimpleAttribute("type", "_last");

	public ObjectList<GraphObject, GraphLink> GetLastObject()
	{
		return GetOutNeighbors().Filter(last_type);
	}

	public void SetLast(Object value, long ctime)
	{
		ObjectList<GraphObject, GraphLink> list = GetLastObject();

		if(list.size() == 0)
		{
			GraphObject last = graph_service.AddObject();
			graph_service.AddLink(GetBaseObject(), last, last_type);
		}

		GraphObject object = list.Only();

		object.SetAttribute(AbstractVaryingAttribute.value_const, value);
		object.SetAttribute(AbstractVaryingAttribute.ctime_const, ctime);
	}

	@Nullable
	public GraphObject GetLast()
	{
		ObjectList<GraphObject, GraphLink> list = GetLastObject();

		if(list.size() == 0)
			return null;

		return list.Only();
	}

	@Override
	public LinkList GetInLinks()
	{
		return null;
	}

	@Override
	public LinkList GetOutLinks()
	{
		return null;
	}

	@Override
	public ObjectList GetInNeighbors()
	{
		return null;
	}

	@Override
	public ObjectList GetOutNeighbors()
	{
		return null;
	}

	@Override
	public ObjectList GetInNeighbors(String link_name, Object link_value)
	{
		return null;
	}

	@Override
	public ObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		return null;
	}

	@Override
	public ObjectList GetInNeighbors(String link_name)
	{
		return null;
	}

	@Override
	public ObjectList GetOutNeighbors(String link_name)
	{
		return null;
	}

	@Override
	public ObjectList GetInNeighbors(SimpleAttribute link_attribute)
	{
		return null;
	}

	@Override
	public ObjectList GetOutNeighbors(SimpleAttribute link_attribute)
	{
		return null;
	}
}
