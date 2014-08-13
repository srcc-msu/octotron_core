/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.graph.IAttribute;
import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.*;

/**
 * base class for entity-specific lists<br>
 * contains some general operations<br>
 * */
public abstract class IEntityList<T extends IEntity> implements Iterable<T>
{
	protected final List<T> list;

	protected IEntityList()
	{
		list = new LinkedList<>();
	}

	protected IEntityList(List<T> list)
	{
		this.list = list;
	}

	protected IEntityList(IEntityList<T> list)
	{
		this.list = new LinkedList<>();
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

	@Override
	public final Iterator<T> iterator()
	{
		return list.iterator();
	}

	public abstract IEntityList<T> range(int from, int to);
	public abstract IEntityList<T> ranges(int... ranges);
	public abstract IEntityList<T> Filter(SimpleAttribute att, EQueryType type);
	public abstract IEntityList<T> Filter(String name, Object value, EQueryType type);
	public abstract IEntityList<T> Filter(SimpleAttribute att);
	public abstract IEntityList<T> Filter(String name, Object value);
	public abstract IEntityList<T> Filter(String name);
	public abstract IEntityList<T> Uniq();

/**
 * get \OctoAttributeList, containing all attributes with name \name<br>
 * in any list object<br>
 * order of attributes is the same as order of objects<br>
 * */
	public final AttributeList<IAttribute> GetAttributes(String name)
	{
		AttributeList<IAttribute> attributes = new AttributeList<>();

		for(T att : list)
		{
			if(att.TestAttribute(name))
				attributes.add(att.GetAttribute(name));
		}

		return attributes;
	}

/**
 * checks that list contains only one element and returns it<br>
 * if it is not true - throws the exception<br>
 * the function is for use in places where there MUST be only one element,<br>
 * if the model is correct<br>
 * */
	public final T Only()
	{
		if(size() > 1)
			throw new ExceptionModelFail("list contains few elements");

		if(size() == 0)
			throw new ExceptionModelFail("list does not contains elements");

		return get(0);
	}

	protected List<T> InnerAppend(List<T> list2)
	{
		List<T> new_list = new LinkedList<>(list);

		new_list.addAll(list2);
		return new_list;
	}

	protected List<T> InnerRange(int from, int to)
	{
		return new LinkedList<>(list.subList(from, to));
	}

	protected List<T> InnerRanges(int... ranges)
	{
		if(ranges.length % 2 != 0)
			throw new ExceptionModelFail("even amount of arguments must be provided");

		List<T> new_list = new LinkedList<>();

		for(int i = 0; i < ranges.length; i += 2)
			new_list.addAll(list.subList(ranges[i], ranges[i+1]));

		return new_list;
	}

	protected List<T> InnerElems(int... elems)
	{
		List<T> new_list = new LinkedList<>();

		for(int elem : elems)
			new_list.add(list.get(elem));

		return new_list;
	}

	private boolean CheckOp(T obj, String name, Object value, EQueryType type)
	{
		if(!obj.TestAttribute(name))
			return false;

		IAttribute attr = obj.GetAttribute(name);

		switch(type)
		{
			case EQ:
				return attr.eq(value);
			case NE:
				return attr.ne(value);
			case GE:
				return attr.ge(value);
			case GT:
				return attr.gt(value);
			case LE:
				return attr.le(value);
			case LT:
				return attr.lt(value);
			case NONE:
			default:
				throw new ExceptionModelFail("unsupported operation for list filter: " + type);
		}
	}

	protected List<T> InnerFilter(String name, Object value, EQueryType type)
	{
		if(name == null)
			return list;

		if(value == null)
			return InnerFilter(name);

		List<T> new_list = new LinkedList<>();

		for(T obj : list)
		{
			if(CheckOp(obj, name, value, type))
				new_list.add(obj);
		}

		return new_list;
	}

	protected List<T> InnerFilter(String name)
	{
		if(name == null)
			return list;

		List<T> new_list = new LinkedList<>();

		for(T obj : list)
		{
			if(obj.TestAttribute(name))
				new_list.add(obj);
		}

		return new_list;
	}

	protected List<T> InnerUniq()
	{
		Map<Long, T> map = new LinkedHashMap<>();

		for (T elem : list)
			map.put(elem.GetUID().getUid(), elem);

		List<T> new_list = new LinkedList<>();
		new_list.addAll(map.values());

		return new_list;
	}

	public static enum EQueryType { EQ, NE, LE, GE, LT, GT, NONE }
}
