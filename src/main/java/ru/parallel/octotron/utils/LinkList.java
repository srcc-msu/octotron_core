/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.utils;

import java.util.List;

import main.java.ru.parallel.octotron.core.OctoLink;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class LinkList extends AbsEntityList<OctoLink>
{
	public LinkList(List<OctoLink> list)
	{
		super(list);
	}

	public LinkList()
	{
		super();
	}

	public LinkList append(LinkList list2)
	{
		InnerAppend(list2.list);
		return this;
	}

	public LinkList range(int from, int to)
	{
		return new LinkList(InnerRange(from, to));
	}

	public LinkList ranges(int... ranges)
	{
		return new LinkList(InnerRanges(ranges));
	}

	public LinkList Filter(SimpleAttribute att, EQueryType type)
	{
		return new LinkList(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public LinkList Filter(String name, Object value, EQueryType type)
	{
		return new LinkList(InnerFilter(name, value, type));
	}

	public LinkList Filter(SimpleAttribute att)
	{
		return new LinkList(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public LinkList Filter(String name, Object value)
	{
		return new LinkList(InnerFilter(name, value, EQueryType.EQ));
	}

	public LinkList Filter(String name)
	{
		return new LinkList(InnerFilter(name));
	}

	public LinkList Uniq()
	{
		LinkList new_list = new LinkList(InnerUniq());
		return new_list;
	}

	public ObjectList Target()
	{
		ObjectList new_list = new ObjectList();

		for(OctoLink link : list)
			new_list.add(link.Target());

		return new_list;
	}

	public ObjectList Source()
	{
		ObjectList new_list = new ObjectList();

		for(OctoLink link : list)
			new_list.add(link.Source());

		return new_list;
	}
}
