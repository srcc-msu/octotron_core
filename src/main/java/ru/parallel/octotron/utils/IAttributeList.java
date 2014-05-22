/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.util.*;

import ru.parallel.octotron.primitive.SimpleAttribute;

public abstract class IAttributeList<T extends SimpleAttribute> implements Iterable<T>
{
	protected List<T> list;

	protected IAttributeList()
	{
		list = new LinkedList<T>();
	}

	protected IAttributeList(List<T> list)
	{
		this.list = list;
	}

	protected IAttributeList(IAttributeList<T> list)
	{
		this.list = new LinkedList<T>();
		this.list.addAll(list.list);
	}

	public final void add(T t)
	{
		list.add(t);
	}

	public final T get(int n)
	{
		return list.get(n);
	}

	public final int size()
	{
		return list.size();
	}

	public final Iterator<T> iterator()
	{
		return list.iterator();
	}

	public abstract IAttributeList<T> AlphabeticSort();

	protected final List<T> InnerAppend(List<T> list2)
	{
		List<T> new_list = new LinkedList<T>(list);

		new_list.addAll(list2);
		return new_list;
	}

	protected final List<T> InnerAlphabeticSort()
	{
		List<T> new_list = new LinkedList<T>(list);

		Collections.sort(new_list, new Comparator<T>()
		{
			public int compare(T o1, T o2)
			{
				return o1.GetName().compareTo(o2.GetName());
			}
		});

		return new_list;
	}
}
