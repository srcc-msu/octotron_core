/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.attributes.IAttribute;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.*;

public final class AttributeList<T extends IAttribute> implements Iterable<T>
{
	private final List<T> list;

	public AttributeList()
	{
		list = new LinkedList<>();
	}

	private AttributeList(List<T> list)
	{
		this.list = list;
	}

	public AttributeList(AttributeList<T> list)
	{
		this.list = new LinkedList<>();
		this.list.addAll(list.list);
	}

	public final void add(T t)
	{
		list.add(t);
	}

	public final AttributeList<T> append(AttributeList<? extends T> list2)
	{
		List<T> new_list = new LinkedList<>(list);

		new_list.addAll(list2.list);
		return new AttributeList<>(new_list);
	}

	public final void addAll(AttributeList<? extends T> list2)
	{
		list.addAll(list2.list);
	}

	public final T get(int n)
	{
		return list.get(n);
	}

	public final int size()
	{
		return list.size();
	}

	@Override
	public final Iterator<T> iterator()
	{
		return list.iterator();
	}

	final List<T> InnerAlphabeticSort()
	{
		List<T> new_list = new LinkedList<>(list);

		Collections.sort(new_list, new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				return o1.GetName().compareTo(o2.GetName());
			}
		});

		return new_list;
	}

	public AttributeList<T> ge(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.ge(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> le(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.le(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> gt(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.gt(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> lt(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.lt(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> eq(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.eq(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> ne(Object val)
	{
		List<T> new_list = new LinkedList<>();

		for(T att : list)
			if(att.ne(val))
				new_list.add(att);

		return new AttributeList<>(new_list);
	}

	public AttributeList<T> AlphabeticSort()
	{
		return new AttributeList<>(InnerAlphabeticSort());
	}

	public List<SimpleAttribute> ToSimple()
	{
		List<SimpleAttribute> result = new LinkedList<>();

		for(T attribute : list)
			result.add(new SimpleAttribute(attribute.GetName(), attribute.GetValue()));

		return result;
	}
}
