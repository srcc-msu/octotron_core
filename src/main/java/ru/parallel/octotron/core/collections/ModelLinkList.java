/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.model.ModelLink;

import java.util.List;

public class ModelLinkList extends ModelList<ModelLink, ModelLinkList>
{
	public ModelLinkList()
	{
		super();
	}

	/**
	 * list copy
	 * */
	public ModelLinkList(ModelLinkList list)
	{
		super(list);
	}


	/**
	 * list reuse
	 * */
	public ModelLinkList(List<ModelLink> graph_links)
	{
		super(graph_links);
	}

	@Override
	protected ModelLinkList Instance(List<ModelLink> new_list)
	{
		return new ModelLinkList(new_list);
	}

	public ModelObjectList Target()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelLink link : list)
			new_list.add(link.Target());

		return new_list;
	}

	public ModelObjectList Source()
	{
		ModelObjectList new_list = new ModelObjectList();

		for(ModelLink link : list)
			new_list.add(link.Source());

		return new_list;
	}
}
