/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.core;

import main.java.ru.parallel.octotron.primitive.Uid;

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

	public OctoObject Target()
	{
		return graph_service.GetLinkTarget(this);
	}

	public OctoObject Source()
	{
		return graph_service.GetLinkSource(this);
	}
}
