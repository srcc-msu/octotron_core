/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

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
public class ObjectList<OT extends IObject<OT, LT>, LT extends ILink<OT, LT>> extends IEntityList<OT>
{
	public ObjectList()
	{
		super();
	}

	private ObjectList(List<OT> list)
	{
		super(list);
	}

	public ObjectList(ObjectList<OT, LT> list)
	{
		super(list);
	}

	public ObjectList<OT, LT> append(ObjectList<OT, LT> list)
	{
		return new ObjectList<>(InnerAppend(list.list));
	}

	public ObjectList<OT, LT> range(int from, int to)
	{
		return new ObjectList<>(InnerRange(from, to));
	}

	public ObjectList<OT, LT> ranges(int... ranges)
	{
		return new ObjectList<>(InnerRanges(ranges));
	}

	public ObjectList<OT, LT> elems(int... elems)
	{
		return new ObjectList<>(InnerElems(elems));
	}

	public ObjectList<OT, LT> Filter(SimpleAttribute att, EQueryType type)
	{
		return new ObjectList<>(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public ObjectList<OT, LT> Filter(String name, Object value, EQueryType type)
	{
		return new ObjectList<>(InnerFilter(name, value, type));
	}

	public ObjectList<OT, LT> Filter(SimpleAttribute att)
	{
		return new ObjectList<>(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public ObjectList<OT, LT> Filter(String name, Object value)
	{
		return new ObjectList<>(InnerFilter(name, value, EQueryType.EQ));
	}

	public ObjectList<OT, LT> Filter(String name)
	{
		return new ObjectList<>(InnerFilter(name));
	}

	public ObjectList<OT, LT> GetInNeighbors(String link_name, Object link_value)
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name, link_value));

		return new_list;
	}

	public ObjectList<OT, LT> GetOutNeighbors(String link_name, Object link_value)
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name, link_value));

		return new_list;
	}

	public ObjectList<OT, LT> GetInNeighbors(SimpleAttribute attr)
	{
		return GetInNeighbors(attr.GetName(), attr.GetValue());
	}

	public ObjectList<OT, LT> GetOutNeighbors(SimpleAttribute attr)
	{
		return GetOutNeighbors(attr.GetName(), attr.GetValue());
	}

	public ObjectList<OT, LT> GetInNeighbors(String link_name)
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name));

		return new_list;
	}

	public ObjectList<OT, LT> GetOutNeighbors(String link_name)
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name));

		return new_list;
	}

	public ObjectList<OT, LT> GetInNeighbors()
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetInNeighbors());

		return new_list;
	}

	public ObjectList<OT, LT> GetOutNeighbors()
	{
		ObjectList<OT, LT> new_list = new ObjectList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetOutNeighbors());

		return new_list;
	}

	public ObjectList<OT, LT> Uniq()
	{
		return new ObjectList<>(InnerUniq());
	}

	public LinkList<OT, LT> GetInLinks()
	{
		LinkList<OT, LT> new_list = new LinkList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetInLinks());

		return new_list;
	}

	public LinkList<OT, LT> GetOutLinks()
	{
		LinkList<OT, LT> new_list = new LinkList<>();

		for(OT obj : list)
			new_list = new_list.append(obj.GetOutLinks());

		return new_list;
	}
}
