/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import ru.parallel.octotron.core.OctoAttribute;
import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.generators.CsvFiller;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;

/**
 * base class for entity-specific lists<br>
 * contains some general operations<br>
 * */
public abstract class IEntityList<T extends OctoEntity> implements Iterable<T>
{
	protected final List<T> list;

	protected IEntityList()
	{
		list = new LinkedList<T>();
	}

	protected IEntityList(List<T> list)
	{
		this.list = list;
	}

	protected IEntityList(IEntityList<T> list)
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

/**
 * get \OctoAttributeList, containing all attributes with name \name<br>
 * in any list object<br>
 * order of attributes is the same as order of objects<br>
 * */
	public final OctoAttributeList GetAttributes(String name)
	{
		OctoAttributeList attributes = new OctoAttributeList();

		for(T att : list)
		{
			if(att.TestAttribute(name))
				attributes.add(att.GetAttribute(name));
		}

		return attributes;
	}

/**
 * set the same attribute \name = \value to all list elements<br>
 * */
	public final void SetAttributes(String name, Object value)
	{
		for(T obj : list)
			obj.DeclareAttribute(name, value);
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
		{
			System.err.println(AutoFormat.PrintNL(this, new OctoAttributeList()));

			throw new ExceptionModelFail("list contains few elements");
		}

		if(size() == 0)
			throw new ExceptionModelFail("list does not contains elements");

		return get(0);
	}

/**
 * allows to set different attributes to all list elements from csv file<br>
 * see {@link CsvFiller} for details
 * */
	public final void SetAttributesFromCsv(String file_name)
		throws ExceptionParseError
	{
		try
		{
			CsvFiller.Read(file_name, this);
		}
		catch(FileNotFoundException ex)
		{
			throw new ExceptionModelFail("config file not found " + file_name);
		}
		catch (IOException e)
		{
			throw new ExceptionModelFail("error reading config file " + file_name);
		}
	}

	protected List<T> InnerAppend(List<T> list2)
	{
		List<T> new_list = new LinkedList<T>(list);

		new_list.addAll(list2);
		return new_list;
	}

	protected List<T> InnerRange(int from, int to)
	{
		return new LinkedList<T>(list.subList(from, to));
	}

	protected List<T> InnerRanges(int... ranges)
	{
		if(ranges.length % 2 != 0)
			throw new ExceptionModelFail("even amount of arguments must be provided");

		List<T> new_list = new LinkedList<T>();

		for(int i = 0; i < ranges.length; i += 2)
			new_list.addAll(list.subList(ranges[i], ranges[i+1]));

		return new_list;
	}

	protected List<T> InnerElems(int... elems)
	{
		List<T> new_list = new LinkedList<T>();

		for(int elem : elems)
			new_list.add(list.get(elem));

		return new_list;
	}

	private boolean CheckOp(T obj, String name, Object value, EQueryType type)
	{
		if(!obj.TestAttribute(name))
			return false;

		OctoAttribute attr = obj.GetAttribute(name);

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

		List<T> new_list = new LinkedList<T>();

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

		List<T> new_list = new LinkedList<T>();

		for(T obj : list)
		{
			if(obj.TestAttribute(name))
				new_list.add(obj);
		}

		return new_list;
	}

	protected List<T> InnerUniq()
	{
		Map<Long, T> map = new LinkedHashMap<Long, T>();

		for (T elem : list)
			map.put(elem.GetUID().getUid(), elem);

		List<T> new_list = new LinkedList<T>();
		new_list.addAll(map.values());

		return new_list;
	}

	public static enum EQueryType { EQ, NE, LE, GE, LT, GT, NONE }
}
