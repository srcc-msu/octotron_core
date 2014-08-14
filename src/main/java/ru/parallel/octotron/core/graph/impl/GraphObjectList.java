/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class GraphObjectList extends EntityList<GraphObject, GraphObjectList>
{
	private GraphObjectList(List<GraphObject> list)
	{
		super(list);
	}

	@Override
	protected GraphObjectList Instance(List<GraphObject> new_list)
	{
		return new GraphObjectList(new_list);
	}

	public GraphObjectList()
	{
		super();
	}

	public GraphObjectList GetInNeighbors(String link_name, Object link_value)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name, link_value));

		return new_list;
	}

	public GraphObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name, link_value));

		return new_list;
	}

	public GraphObjectList GetInNeighbors(SimpleAttribute attr)
	{
		return GetInNeighbors(attr.GetName(), attr.GetValue());
	}

	public GraphObjectList GetOutNeighbors(SimpleAttribute attr)
	{
		return GetOutNeighbors(attr.GetName(), attr.GetValue());
	}

	public GraphObjectList GetInNeighbors(String link_name)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name));

		return new_list;
	}

	public GraphObjectList GetOutNeighbors(String link_name)
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name));

		return new_list;
	}

	public GraphObjectList GetInNeighbors()
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors());

		return new_list;
	}

	public GraphObjectList GetOutNeighbors()
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors());

		return new_list;
	}

	public GraphLinkList GetInLinks()
	{
		GraphLinkList new_list = new GraphLinkList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetInLinks());

		return new_list;
	}

	public GraphLinkList GetOutLinks()
	{
		GraphLinkList new_list = new GraphLinkList();

		for(GraphObject obj : list)
			new_list = new_list.append(obj.GetOutLinks());

		return new_list;
	}
}
