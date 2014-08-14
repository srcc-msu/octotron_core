 /*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.impl;

 import ru.parallel.octotron.core.collections.EntityList;
import ru.parallel.octotron.core.model.ModelEntity;
 import ru.parallel.octotron.core.model.impl.ModelLinkList;
 import ru.parallel.octotron.core.model.impl.ModelObjectList;

 import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class GraphList extends EntityList<GraphEntity, GraphList>
{
	public GraphList()
	{
		super();
	}

	protected GraphList(List<GraphEntity> graph_links)
	{
		super(graph_links);
	}

	public GraphList append(GraphObjectList list)
	{
		return new GraphList(InnerAppend(list.GetList()));
	}

	public GraphList append(GraphLinkList list)
	{
		return new GraphList(InnerAppend(list.GetList()));
	}

	@Override
	protected GraphList Instance(List<GraphEntity> new_list)
	{
		return new GraphList(new_list);
	}
}
