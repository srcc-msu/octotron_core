/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.util.List;

import ru.parallel.octotron.core.OctoLink;
import ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class OctoLinkList extends IEntityList<OctoLink>
{
	public OctoLinkList(List<OctoLink> list)
	{
		super(list);
	}

	public OctoLinkList()
	{
		super();
	}

	public OctoLinkList append(OctoLinkList list)
	{
		return new OctoLinkList(InnerAppend(list.list));
	}

	public OctoLinkList range(int from, int to)
	{
		return new OctoLinkList(InnerRange(from, to));
	}

	public OctoLinkList ranges(int... ranges)
	{
		return new OctoLinkList(InnerRanges(ranges));
	}

	public OctoLinkList Filter(SimpleAttribute att, EQueryType type)
	{
		return new OctoLinkList(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public OctoLinkList Filter(String name, Object value, EQueryType type)
	{
		return new OctoLinkList(InnerFilter(name, value, type));
	}

	public OctoLinkList Filter(SimpleAttribute att)
	{
		return new OctoLinkList(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public OctoLinkList Filter(String name, Object value)
	{
		return new OctoLinkList(InnerFilter(name, value, EQueryType.EQ));
	}

	public OctoLinkList Filter(String name)
	{
		return new OctoLinkList(InnerFilter(name));
	}

	public OctoLinkList Uniq()
	{
		return new OctoLinkList(InnerUniq());
	}

	public OctoObjectList Target()
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoLink link : list)
			new_list.add(link.Target());

		return new_list;
	}

	public OctoObjectList Source()
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoLink link : list)
			new_list.add(link.Source());

		return new_list;
	}
}
