/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.utils;

import java.util.Iterator;
import java.util.List;

import main.java.ru.parallel.octotron.primitive.SimpleAttribute;

public abstract class AbsAttributeList<T extends SimpleAttribute> implements Iterable<T>
{
	protected List<T> list;

	public void add(T t)
	{
		list.add(t);
	}

	public T get(int n) // ???
	{
		return list.get(n);
	}

	public int size()
	{
		return list.size();
	}

	public Iterator<T> iterator()
	{
		return list.iterator();
	}

	public AbsAttributeList<?> range(int i, int size)
	{
		throw new RuntimeException("??");
	}

	public AbsAttributeList<T> AlphabeticSort()
	{
		throw new RuntimeException("??");
	}

/**
 * modifies the class list
 * */
	protected List<T> InnerAppend(List<T> list2)
	{
		list.addAll(list2);
		return list;
	}

	protected List<T> InnerRange(int from, int to)
	{
		return list.subList(from, to);
	}
}
