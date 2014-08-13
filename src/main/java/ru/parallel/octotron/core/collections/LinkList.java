/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.graph.ILink;
import ru.parallel.octotron.core.graph.IObject;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class LinkList<OT extends IObject<OT, LT>, LT extends ILink<OT, LT>> extends IEntityList<LT>
{
	private LinkList(List<LT> list)
	{
		super(list);
	}

	public LinkList()
	{
		super();
	}

	public LinkList<OT, LT> append(LinkList<OT, LT> list)
	{
		return new LinkList<>(InnerAppend(list.list));
	}

	@Override
	public LinkList<OT, LT> range(int from, int to)
	{
		return new LinkList<>(InnerRange(from, to));
	}

	@Override
	public LinkList<OT, LT> ranges(int... ranges)
	{
		return new LinkList<>(InnerRanges(ranges));
	}

	@Override
	public LinkList<OT, LT> Filter(SimpleAttribute att, EQueryType type)
	{
		return new LinkList<>(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	@Override
	public LinkList<OT, LT> Filter(String name, Object value, EQueryType type)
	{
		return new LinkList<>(InnerFilter(name, value, type));
	}

	@Override
	public LinkList<OT, LT> Filter(SimpleAttribute att)
	{
		return new LinkList<>(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	@Override
	public LinkList<OT, LT> Filter(String name, Object value)
	{
		return new LinkList<>(InnerFilter(name, value, EQueryType.EQ));
	}

	@Override
	public LinkList<OT, LT> Filter(String name)
	{
		return new LinkList<>(InnerFilter(name));
	}

	@Override
	public LinkList<OT, LT> Uniq()
	{
		return new LinkList<>(InnerUniq());
	}

	public ObjectList<OT, LT> Target()
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(LT link : list)
			new_list.add((OT)link.Target());

		return new_list;
	}

	public ObjectList<OT, LT> Source()
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(LT link : list)
			new_list.add((OT)link.Source());

		return new_list;
	}
}
