/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.impl;

import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.model.ModelLink;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class ModelLinkList extends EntityList<ModelLink, ModelLinkList>
{
	public ModelLinkList()
	{
		super();
	}

	public ModelLinkList(List<ModelLink> graph_links)
	{
		super(graph_links);
	}

	@Override
	protected ModelLinkList Instance(List<ModelLink> new_list)
	{
		return new ModelLinkList(new_list);
	}

	@SuppressWarnings("unchecked") // it will always match
	public ModelObjectList Target()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelLink link : list)
			new_list.add(link.Target());

		return new_list;
	}

@SuppressWarnings("unchecked") // it will always match
	public ModelObjectList Source()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelLink link : list)
			new_list.add(link.Source());

		return new_list;
	}
}
