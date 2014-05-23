/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.Uid;

/**
 * implementation of link, that resides in \graph<br>
 * implements {@link OctoLink} interface<br>
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
	public long Update(EDependencyType dep)
	{
		return 0;
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
