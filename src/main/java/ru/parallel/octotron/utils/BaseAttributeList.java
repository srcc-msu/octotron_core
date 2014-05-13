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
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;

public class BaseAttributeList extends AbsAttributeList<SimpleAttribute>
{
	public BaseAttributeList()
	{
		list = new LinkedList<SimpleAttribute>();
	}

	private BaseAttributeList(List<SimpleAttribute> _list)
	{
		this.list = _list;
	}

	public BaseAttributeList(AttributeList list2)
	{
		this();

		for(OctoAttribute att : list2.list)
			list.add(att);
	}

	public BaseAttributeList append(BaseAttributeList list2)
	{
		InnerAppend(list2.list);
		return this;
	}

@Override
	public BaseAttributeList range(int from, int to)
	{
		return new BaseAttributeList(InnerRange(from, to));
	}

@Override
	public BaseAttributeList AlphabeticSort()
	{
		List<SimpleAttribute> new_list = new ArrayList<SimpleAttribute>(list);

		Collections.sort(new_list, new Comparator<SimpleAttribute>()
		{
			public int compare(SimpleAttribute o1, SimpleAttribute o2)
			{
				return o1.GetName().compareTo(o2.GetName());
			}
		});

		return new BaseAttributeList(new_list);
	}
}
