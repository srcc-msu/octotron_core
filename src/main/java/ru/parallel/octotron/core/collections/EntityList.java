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
 * base list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * */
public abstract class EntityList<T extends IEntity<?>, R extends EntityList<T, R>> implements Iterable<T>
{
	protected final List<T> list;

	protected EntityList()
	{
		list = new LinkedList<>();
	}

	protected EntityList(List<T> list)
	{
		this.list = list;
	}

	protected EntityList(R list)
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

	public final Iterator<T> iterator()
	{
		return list.iterator();
	}

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

	protected List<T> InnerAppend(List<? extends T> list2)
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

	protected boolean CheckOp(T obj, String name, Object value, EQueryType type)
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

	public List<T> GetList()
	{
		return list;
	}

	public static enum EQueryType { EQ, NE, LE, GE, LT, GT, NONE }

// -----------
//
// -----------

	public R append(R list)
{
	return Instance(InnerAppend(list.list));
}

	protected abstract R Instance(List<T> new_list);

	public final R range(int from, int to)
	{
		return Instance(InnerRange(from, to));
	}

	public final R ranges(int... ranges)
	{
		return Instance(InnerRanges(ranges));
	}

	public final R Filter(SimpleAttribute att, EQueryType type)
	{
		return Instance(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public final R Filter(String name, Object value, EQueryType type)
	{
		return Instance(InnerFilter(name, value, type));
	}

	public final R Filter(SimpleAttribute att)
	{
		return Instance(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public final R Filter(String name, Object value)
	{
		return Instance(InnerFilter(name, value, EQueryType.EQ));
	}

	public final R Filter(String name)
	{
		return Instance(InnerFilter(name));
	}

	public final R Uniq()
	{
		return Instance(InnerUniq());
	}
}
