/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.utils;

import java.util.List;

import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * only filtering for now, to be honest..<br>
 * */
public class ObjectList extends AbsEntityList<OctoObject>
{
	public ObjectList(List<OctoObject> list)
	{
		super(list);
	}

	public ObjectList(ObjectList list2)
	{
		this();

		for(OctoObject att : list2.list)
			list.add(att);
	}

	public ObjectList()
	{
		super();
	}

	public ObjectList append(ObjectList list2)
	{
		InnerAppend(list2.list);
		return this;
	}

	public ObjectList range(int from, int to)
	{
		return new ObjectList(InnerRange(from, to));
	}

	public ObjectList ranges(int... ranges)
	{
		return new ObjectList(InnerRanges(ranges));
	}

	public ObjectList elems(int... elems)
	{
		return new ObjectList(InnerElems(elems));
	}

	public ObjectList Filter(SimpleAttribute att, EQueryType type)
	{
		return new ObjectList(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public ObjectList Filter(String name, Object value, EQueryType type)
	{
		return new ObjectList(InnerFilter(name, value, type));
	}

	public ObjectList Filter(SimpleAttribute att)
	{
		return new ObjectList(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public ObjectList Filter(String name, Object value)
	{
		return new ObjectList(InnerFilter(name, value, EQueryType.EQ));
	}

	public ObjectList Filter(String name)
	{
		return new ObjectList(InnerFilter(name));
	}

	public ObjectList GetInNeighbors(String link_name, Object link_value)
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetInNeighbors(link_name, link_value));

		return new_list;
	}

	public ObjectList GetOutNeighbors(String link_name, Object link_value)
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetOutNeighbors(link_name, link_value));

		return new_list;
	}

	public ObjectList GetInNeighbors(SimpleAttribute attr)
	{
			return GetInNeighbors(attr.GetName(), attr.GetValue());
	}

	public ObjectList GetOutNeighbors(SimpleAttribute attr)
	{
		return GetOutNeighbors(attr.GetName(), attr.GetValue());
	}

	public ObjectList GetInNeighbors(String link_name)
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetInNeighbors(link_name));

		return new_list;
	}

	public ObjectList GetOutNeighbors(String link_name)
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetOutNeighbors(link_name));

		return new_list;
	}

	public ObjectList GetInNeighbors()
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetInNeighbors());

		return new_list;
	}

	public ObjectList GetOutNeighbors()
	{
		ObjectList new_list = new ObjectList();

		for(OctoObject obj : list)
			new_list.append(obj.GetOutNeighbors());

		return new_list;
	}

	public ObjectList Uniq()
	{
		ObjectList new_list = new ObjectList(InnerUniq());
		return new_list;
	}

	public LinkList GetInLinks()
	{
		LinkList new_list = new LinkList();

		for(OctoObject obj : list)
			new_list.append(obj.GetInLinks());

		return new_list;
	}

	public LinkList GetOutLinks()
	{
		LinkList new_list = new LinkList();

		for(OctoObject obj : list)
			new_list.append(obj.GetOutLinks());

		return new_list;
	}
}
