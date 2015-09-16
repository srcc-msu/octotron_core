/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.graph.impl;

import ru.parallel.octotron.core.primitive.Info;
import ru.parallel.octotron.persistence.graph.EGraphType;
import ru.parallel.octotron.persistence.graph.IGraph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * provides additional features over raw graph interface<br>
 * such as: static object, aid counter and arrays<br>
 * ensures all objects have a correct AID<br>
 * */
public final class GraphService
{
	private final IGraph graph;

	public GraphService(IGraph graph)
	{
		this.graph = graph;
	}

//--------
//       LINKS AND OBJECTS
//--------
	public GraphLink AddLink(GraphObject source, GraphObject target, String link_type)
	{
		return new GraphLink(graph, graph.AddLink(source.GetID(), target.GetID(), link_type));
	}

	public GraphObject AddObject()
	{
		return new GraphObject(graph, graph.AddObject());
	}

//--------
//			INDEX
//--------

	public void EnableLinkIndex(String name)
	{
		graph.GetIndex().EnableLinkIndex(name);
	}

	public void EnableObjectIndex(String name)
	{
		graph.GetIndex().EnableObjectIndex(name);
	}

	public Collection<GraphLink> GetAllLinks()
	{
		return LinksFromUid(graph, graph.GetAllLinks());
	}

	public Collection<GraphObject> GetAllObjects()
	{
		List<GraphObject> result = new LinkedList<>();

		for(GraphObject object : ObjectsFromUid(graph, graph.GetAllObjects()))
				result.add(object);

		return result;
	}

	public GraphLink GetLink(String name, Object value)
	{
		Info<EGraphType> info = graph.GetIndex().GetLink(name, value);
		return new GraphLink(graph, info);
	}

	public Collection<GraphLink> GetLinks(String name)
	{
		return LinksFromUid(graph, graph.GetIndex().GetLinks(name));
	}

	public Collection<GraphLink> GetLinks(String name, Object value)
	{
		return LinksFromUid(graph, graph.GetIndex().GetLinks(name, value));
	}

	public GraphObject GetObject(String name, Object value)
	{
		Info<EGraphType> info = graph.GetIndex().GetObject(name, value);
		return new GraphObject(graph, info);
	}

	public Collection<GraphObject> GetObjects(String name)
	{
		return ObjectsFromUid(graph, graph.GetIndex().GetObjects(name));
	}

	public Collection<GraphObject> GetObjects(String name, Object value)
	{
		return ObjectsFromUid(graph, graph.GetIndex().GetObjects(name, value));
	}

	public Collection<GraphLink> QueryLinks(String name, String value)
	{
		return LinksFromUid(graph, graph.GetIndex().QueryLinks(name, value));
	}

	public Collection<GraphObject> QueryObjects(String name, String value)
	{
		return ObjectsFromUid(graph, graph.GetIndex().QueryObjects(name, value));
	}

	public GraphObject GetLinkTarget(GraphLink link)
	{
		return new GraphObject(graph, graph.GetLinkTarget(link.GetID()));
	}

	public GraphObject GetLinkSource(GraphLink link)
	{
		return new GraphObject(graph, graph.GetLinkSource(link.GetID()));
	}

//--------
//			 Utility
//--------

	public void Clean()
	{
		for(GraphObject obj : GetAllObjects())
			obj.Delete();

		for(GraphLink link : GetAllLinks())
			link.Delete();
	}

//--------
//			converters
//--------

	protected static List<Info<EGraphType>> UidsFromList(Collection<? extends GraphEntity> entities)
	{
		List<Info<EGraphType>> uids = new LinkedList<>();

		for(GraphEntity entity : entities)
		{
			uids.add(entity.GetID());
		}

		return uids;
	}

	protected static Collection<GraphLink> LinksFromUid(IGraph graph, List<Info<EGraphType>> uids)
	{
		List<GraphLink> links = new LinkedList<>();

		for(Info<EGraphType> info : uids)
		{
			links.add(new GraphLink(graph, info));
		}

		return links;
	}

	private static Collection<GraphObject> ObjectsFromUid(IGraph graph, List<Info<EGraphType>> uids)
	{
		List<GraphObject> objects = new LinkedList<>();

		for(Info<EGraphType> info : uids)
		{
			objects.add(new GraphObject(graph, info));
		}

		return objects;
	}
}
