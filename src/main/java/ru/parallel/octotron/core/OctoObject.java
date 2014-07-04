/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core;

import ru.parallel.octotron.impl.PersistenStorage;
import ru.parallel.octotron.primitive.EDependencyType;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.utils.OctoEntityList;
import ru.parallel.octotron.utils.OctoLinkList;
import ru.parallel.octotron.utils.OctoObjectList;

import java.util.List;

/**
 * implementation of object according to real \graph<br>
 * implements {@link OctoObject} interface<br>
 * */
public class OctoObject extends OctoEntity
{

	/**
	 * this constructor MUST not be used manually for -<br>
	 * creating new items -<br>
	 * it is needed to obtain the existing from the \graph<br>
	 * */
	OctoObject(GraphService graph, Uid uid)
	{
		super(graph, uid);
	}

	public OctoLinkList GetInLinks()
	{
		return graph_service.GetInLinks(this);
	}

	public OctoLinkList GetOutLinks()
	{
		return graph_service.GetOutLinks(this);
	}

	public OctoObjectList GetInNeighbors(String link_name
		, Object link_value)
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetInLinks().Filter(link_name, link_value))
			objects.add(link.Source());

		return objects;
	}

	public OctoObjectList GetOutNeighbors(String link_name
		, Object link_value)
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetOutLinks().Filter(link_name, link_value))
			objects.add(link.Target());

		return objects;
	}

	public OctoObjectList GetInNeighbors(String link_name)
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetInLinks().Filter(link_name))
			objects.add(link.Source());

		return objects;
	}

	public OctoObjectList GetOutNeighbors(String link_name)
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetOutLinks().Filter(link_name))
			objects.add(link.Target());

		return objects;
	}

	public OctoObjectList GetInNeighbors()
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetInLinks())
			objects.add(link.Source());

		return objects;
	}

	public OctoObjectList GetOutNeighbors()
	{
		OctoObjectList objects = new OctoObjectList();

		for(OctoLink link : GetOutLinks())
			objects.add(link.Target());

		return objects;
	}

	@Override
	public OctoEntityList GetSurround()
	{
		OctoEntityList surround = new OctoEntityList();

		surround = surround.append(GetInNeighbors());
		surround = surround.append(GetInLinks());
		surround = surround.append(GetOutNeighbors());
		surround = surround.append(GetOutLinks());

		return surround;
	}
}
