/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

import ru.parallel.octotron.core.graph.collections.EntityList;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class GraphLinkList extends EntityList<GraphLink, GraphLinkList>
{
	public GraphLinkList()
	{
		super();
	}

	public GraphLinkList(List<GraphLink> graph_links)
	{
		super(graph_links);
	}

	@Override
	protected GraphLinkList Instance(List<GraphLink> new_list)
	{
		return new GraphLinkList(new_list);
	}

	@SuppressWarnings("unchecked") // it will always match
	public GraphObjectList Target()
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphLink link : list)
			new_list.add(link.Target());

		return new_list;
	}

@SuppressWarnings("unchecked") // it will always match
	public GraphObjectList Source()
	{
		GraphObjectList new_list = new GraphObjectList();

		for(GraphLink link : list)
			new_list.add(link.Source());

		return new_list;
	}
}
