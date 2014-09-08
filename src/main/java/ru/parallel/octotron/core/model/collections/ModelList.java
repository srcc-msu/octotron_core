/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.model.collections;

import ru.parallel.octotron.core.graph.collections.EntityList;
import ru.parallel.octotron.core.model.ModelEntity;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class ModelList extends EntityList<ModelEntity, ModelList>
{
	public ModelList()
	{
		super();
	}

	protected ModelList(List<ModelEntity> graph_links)
	{
		super(graph_links);
	}

	public ModelList append(ModelObjectList list)
	{
		return new ModelList(InnerAppend(list.GetList()));
	}

	public ModelList append(ModelLinkList list)
	{
		return new ModelList(InnerAppend(list.GetList()));
	}

	@Override
	protected ModelList Instance(List<ModelEntity> new_list)
	{
		return new ModelList(new_list);
	}
}
