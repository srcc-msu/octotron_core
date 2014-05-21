/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.parallel.octotron.core.OctoAttribute;

/**
 * implements list container for attributes<br>
 * and provides some additional functionality, like filtering<br>
 * only filtering for now, to be honest..<br>
 * */
public class OctoAttributeList extends IAttributeList<OctoAttribute>
{
	public OctoAttributeList()
	{
		super();
	}

	private OctoAttributeList(List<OctoAttribute> list)
	{
		super(list);
	}

	public OctoAttributeList(OctoAttributeList list)
	{
		super(list);
	}

	public OctoAttributeList ge(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.ge(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList le(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.le(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList gt(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.gt(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList lt(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.lt(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList eq(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.eq(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList ne(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.ne(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList append(OctoAttributeList list2)
	{
		return new OctoAttributeList(InnerAppend(list2.list));
	}

@Override
	public OctoAttributeList range(int from, int to)
	{
		return new OctoAttributeList(InnerRange(from, to));
	}

@Override
	public OctoAttributeList AlphabeticSort()
	{
		return new OctoAttributeList(InnerAlphabeticSort());

	}
}

