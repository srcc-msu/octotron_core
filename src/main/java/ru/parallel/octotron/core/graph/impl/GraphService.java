/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.collections.AttributeList;
import ru.parallel.octotron.core.graph.collections.EntityList;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.IGraph;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.Uid;
import ru.parallel.octotron.core.primitive.exception.ExceptionDBError;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.LinkedList;
import java.util.List;

/**
 * provides additional features over raw graph interface<br>
 * such as: static object, aid counter and arrays<br>
 * ensures all objects have a correct AID<br>
 * */
public final class GraphService
{
	private static GraphService INSTANCE = null;
	public static void Init(IGraph graph)
	{
		GraphService.INSTANCE = new GraphService(graph);
	}

	public static GraphService Get()
	{
		return INSTANCE;
	}

// ------------------

	private final IGraph graph;
	private GraphObject static_obj = null;

	private GraphService(IGraph graph)
	{
		this.graph = graph;
		InitStatic();
	}

	private static final String NEXT_AID = "_static_next_AID";

/**
 * return next value for AID counter from static object<br>
 * increment and store new value in db<br>
 * */
	private long NextAID()
	{
		if(!static_obj.TestAttribute(GraphService.NEXT_AID))
			static_obj.DeclareAttribute(GraphService.NEXT_AID, 0L);

		long next_AID = static_obj.GetAttribute(GraphService.NEXT_AID).GetLong();
		static_obj.UpdateAttribute(GraphService.NEXT_AID, next_AID + 1);

		return next_AID;
	}

//---------------------------------
//          STATIC
//---------------------------------

	private static final String STATIC_PREFIX = "_static_";

/**
 * create static object if it does not exist<br>
 * otherwise - find existing in the graph <br>
 * */
	private void InitStatic()
	{
		if(static_obj == null)
		{
			EnableObjectIndex("type");
			GraphObjectList objects = GetObjects("type", GraphService.STATIC_PREFIX);

			if(objects.size() == 0)
			{
// have to do it manually - no AID yet
				static_obj = new GraphObject(graph, graph.AddObject());

				static_obj.DeclareAttribute("type", GraphService.STATIC_PREFIX);
				static_obj.DeclareAttribute("AID", NextAID());
			}
			else if(objects.size() > 1)
				throw new ExceptionDBError("found multiple static objects");
			else
				static_obj = objects.get(0);
		}
	}

	private boolean IsStaticObject(GraphEntity entity)
	{
		return entity.equals(static_obj);
	}

	private void DeleteStatic()
	{
		graph.DeleteObject(static_obj.GetUID());
		static_obj = null;
	}

//---------------------
//       LINKS AND OBJECTS
//---------------------
	public GraphLink AddLink(GraphObject source, GraphObject target, String link_type)
	{
		GraphLink link = new GraphLink(graph, graph.AddLink(source.GetUID(), target.GetUID(), link_type));
		link.DeclareAttribute("AID", NextAID());

		return link;
	}

	public GraphLink AddLink(GraphObject source, GraphObject target, SimpleAttribute link_type)
	{
		if(!link_type.GetName().equals("type"))
			throw new ExceptionModelFail("the only acceptable attribute for a new links is 'type'");

		return AddLink(source, target, (String)link_type.GetValue());
	}

	public GraphObject AddObject()
	{
		GraphObject object = new GraphObject(graph, graph.AddObject());
		object.DeclareAttribute("AID", NextAID());

		return object;
	}

// --------------------------------
//			LABEL
//---------------------------------

	public GraphObjectList GetAllLabeledNodes(String label)
	{
		return ObjectsFromUid(graph, graph.GetAllLabeledNodes(label));
	}

// --------------------------------
//			INDEX
//---------------------------------

	public void EnableLinkIndex(String name)
	{
		graph.GetIndex().EnableLinkIndex(name);
	}

	public void EnableObjectIndex(String name)
	{
		graph.GetIndex().EnableObjectIndex(name);
	}

	public GraphLinkList GetAllLinks()
	{
		return LinksFromUid(graph, graph.GetAllLinks());
	}

	public GraphObjectList GetAllObjects()
	{
		GraphObjectList result = new GraphObjectList();

		for(GraphObject object : ObjectsFromUid(graph, graph.GetAllObjects()))
			if(!IsStaticObject(object))
				result.add(object);

		return result;
	}

	public GraphLink GetLink(SimpleAttribute att)
	{
		return GetLink(att.GetName(), att.GetValue());
	}

	public GraphLink GetLink(String name, Object value)
	{
		Uid uid = graph.GetIndex().GetLink(name, value);
		return new GraphLink(graph, uid);
	}

	public GraphLinkList GetLinks(SimpleAttribute att)
	{
		return GetLinks(att.GetName(), att.GetValue());
	}

	public GraphLinkList GetLinks(String name)
	{
		return LinksFromUid(graph, graph.GetIndex().GetLinks(name));
	}

	public GraphLinkList GetLinks(String name, Object value)
	{
		return LinksFromUid(graph, graph.GetIndex().GetLinks(name, value));
	}

	public GraphObject GetObject(SimpleAttribute att)
	{
		return GetObject(att.GetName(), att.GetValue());
	}

	public GraphObject GetObject(String name, Object value)
	{
		Uid uid = graph.GetIndex().GetObject(name, value);
		return new GraphObject(graph, uid);
	}

	public GraphObjectList GetObjects(SimpleAttribute att)
	{
		return GetObjects(att.GetName(), att.GetValue());
	}

	public GraphObjectList GetObjects(String name)
	{
		return ObjectsFromUid(graph, graph.GetIndex().GetObjects(name));
	}

	public GraphObjectList GetObjects(String name, Object value)
	{
		return ObjectsFromUid(graph, graph.GetIndex().GetObjects(name, value));
	}

	public GraphLinkList QueryLinks(String name, String value)
	{
		return LinksFromUid(graph, graph.GetIndex().QueryLinks(name, value));
	}

	public GraphObjectList QueryObjects(String name, String value)
	{
		return ObjectsFromUid(graph, graph.GetIndex().QueryObjects(name, value));
	}

	public GraphObject GetLinkTarget(GraphLink link)
	{
		return new GraphObject(graph, graph.GetLinkTarget(link.GetUID()));
	}

	public GraphObject GetLinkSource(GraphLink link)
	{
		return new GraphObject(graph, graph.GetLinkSource(link.GetUID()));
	}

// ---------------------------------
//			 Utility
// ---------------------------------

	public void Clean()
	{
		for(GraphObject obj : GetAllObjects())
			obj.Delete();

		for(GraphLink link : GetAllLinks())
			link.Delete();

		DeleteStatic();
		InitStatic();
	}

// --------------------------------
//			converters
//---------------------------------

	protected static List<Uid> UidsFromList(EntityList<? extends GraphEntity, ?> entities)
	{
		List<Uid> uids = new LinkedList<>();

		for(IEntity<?> entity : entities)
		{
			uids.add(entity.GetUID());
		}

		return uids;
	}

	protected static GraphLinkList LinksFromUid(IGraph graph, List<Uid> uids)
	{
		GraphLinkList links = new GraphLinkList();

		for(Uid uid : uids)
		{
			links.add(new GraphLink(graph, uid));
		}

		return links;
	}

	private static GraphObjectList ObjectsFromUid(IGraph graph, List<Uid> uids)
	{
		GraphObjectList objects = new GraphObjectList();

		for(Uid uid : uids)
		{
			objects.add(new GraphObject(graph, uid));
		}

		return objects;
	}


	protected static AttributeList<GraphAttribute> AttributesFromPair(GraphEntity parent, List<String> names)
	{
		AttributeList<GraphAttribute> objects = new AttributeList<>();

		for(String name : names)
		{
			objects.add(new GraphAttribute(parent, name));
		}

		return objects;
	}

	public String ExportDot()
	{
		return graph.ExportDot(UidsFromList(GetAllObjects()));
	}

	public String ExportDot(GraphObjectList list)
	{
		return graph.ExportDot(UidsFromList(list));
	}
}
