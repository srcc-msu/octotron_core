/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import main.java.ru.parallel.octotron.core.OctoAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;

/**
 * implements list container for attributes<br>
 * and provides some additional functionality, like filtering<br>
 * only filtering for now, to be honest..<br>
 * */
public class AttributeList extends AbsAttributeList<OctoAttribute>
{
	public AttributeList()
	{
		list = new LinkedList<OctoAttribute>();
	}

	private AttributeList(List<OctoAttribute> _list)
	{
		this.list = _list;
	}

	public AttributeList(AttributeList list2)
	{
		this();

		for(OctoAttribute att : list2.list)
			list.add(att);
	}

	public AttributeList ge(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.ge(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList le(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.le(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList gt(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.gt(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList lt(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.lt(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList eq(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.eq(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList ne(Object val)
		throws ExceptionModelFail
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>();

		for(OctoAttribute att : list)
			if(att.ne(val))
				new_list.add(att);

		return new AttributeList(new_list);
	}

	public AttributeList append(AttributeList list2)
	{
		list.addAll(list2.list);

		return this;
	}

@Override
	public AttributeList range(int from, int to)
	{
		List<OctoAttribute> new_list = list.subList(from, to);
		return new AttributeList(new_list);
	}

@Override
	public AttributeList AlphabeticSort()
	{
		List<OctoAttribute> new_list = new ArrayList<OctoAttribute>(list);

		Collections.sort(new_list, new Comparator<OctoAttribute>()
		{
			public int compare(OctoAttribute o1, OctoAttribute o2)
			{
				return o1.GetName().compareTo(o2.GetName());
			}
		});

		return new AttributeList(new_list);
	}
}

