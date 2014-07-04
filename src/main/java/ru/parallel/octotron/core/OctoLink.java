/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.utils.OctoEntityList;

/**
 * implementation of link, that resides in \graph<br>
 * */
public class OctoLink extends OctoEntity
{
	/**
	 * this constructor MUST not be used manually for -<br>
	 * creating new items -<br>
	 * it is needed to obtain the existing from the \graph<br>
	 * */
	OctoLink(GraphService graph, Uid uid)
	{
		super(graph, uid);
	}

	@Override
	public OctoEntityList GetSurround()
	{
		OctoEntityList surround = new OctoEntityList();

		surround.add(Target());
		surround.add(Source());

		return surround;
	}

	public OctoObject Target()
	{
		return graph_service.GetLinkTarget(this);
	}

	public OctoObject Source()
	{
		return graph_service.GetLinkSource(this);
	}
}
