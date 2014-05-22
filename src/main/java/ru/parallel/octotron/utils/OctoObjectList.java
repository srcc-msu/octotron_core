/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.util.List;

import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class OctoObjectList extends IEntityList<OctoObject>
{
	public OctoObjectList()
	{
		super();
	}

	private OctoObjectList(List<OctoObject> list)
	{
		super(list);
	}

	public OctoObjectList(OctoObjectList list)
	{
		super(list);
	}

	public OctoObjectList append(OctoObjectList list)
	{
		return new OctoObjectList(InnerAppend(list.list));
	}

	public OctoObjectList range(int from, int to)
	{
		return new OctoObjectList(InnerRange(from, to));
	}

	public OctoObjectList ranges(int... ranges)
	{
		return new OctoObjectList(InnerRanges(ranges));
	}

	public OctoObjectList elems(int... elems)
	{
		return new OctoObjectList(InnerElems(elems));
	}

	public OctoObjectList Filter(SimpleAttribute att, EQueryType type)
	{
		return new OctoObjectList(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public OctoObjectList Filter(String name, Object value, EQueryType type)
	{
		return new OctoObjectList(InnerFilter(name, value, type));
	}

	public OctoObjectList Filter(SimpleAttribute att)
	{
		return new OctoObjectList(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public OctoObjectList Filter(String name, Object value)
	{
		return new OctoObjectList(InnerFilter(name, value, EQueryType.EQ));
	}

	public OctoObjectList Filter(String name)
	{
		return new OctoObjectList(InnerFilter(name));
	}

	public OctoObjectList GetInNeighbors(String link_name, Object link_value)
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name, link_value));

		return new_list;
	}

	public OctoObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name, link_value));

		return new_list;
	}

	public OctoObjectList GetInNeighbors(SimpleAttribute attr)
	{
		return GetInNeighbors(attr.GetName(), attr.GetValue());
	}

	public OctoObjectList GetOutNeighbors(SimpleAttribute attr)
	{
		return GetOutNeighbors(attr.GetName(), attr.GetValue());
	}

	public OctoObjectList GetInNeighbors(String link_name)
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors(link_name));

		return new_list;
	}

	public OctoObjectList GetOutNeighbors(String link_name)
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors(link_name));

		return new_list;
	}

	public OctoObjectList GetInNeighbors()
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetInNeighbors());

		return new_list;
	}

	public OctoObjectList GetOutNeighbors()
	{
		OctoObjectList new_list = new OctoObjectList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetOutNeighbors());

		return new_list;
	}

	public OctoObjectList Uniq()
	{
		return new OctoObjectList(InnerUniq());
	}

	public OctoLinkList GetInLinks()
	{
		OctoLinkList new_list = new OctoLinkList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetInLinks());

		return new_list;
	}

	public OctoLinkList GetOutLinks()
	{
		OctoLinkList new_list = new OctoLinkList();

		for(OctoObject obj : list)
			new_list = new_list.append(obj.GetOutLinks());

		return new_list;
	}
}
