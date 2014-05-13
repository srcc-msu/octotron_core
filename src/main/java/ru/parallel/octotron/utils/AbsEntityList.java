/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package main.java.ru.parallel.octotron.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import main.java.ru.parallel.octotron.core.OctoEntity;
import main.java.ru.parallel.octotron.impl.generators.CsvFiller;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionDBError;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionParseError;

/**
 * base class for entity-specific lists<br>
 * contains some general operations<br>
 * */
public abstract class AbsEntityList<T extends OctoEntity> implements Iterable<T>
{
	protected List<T> list;

	protected AbsEntityList()
	{
		list = new ArrayList<T>();
	}

	protected AbsEntityList(List<T> list)
	{
		this.list = list;
	}

/**
 * add element to the list<br>
 * */
	public final void add(T t)
	{
		list.add(t);
	}

/**
 * remove element by index<br>
 * */
	public final void remove(int index)
	{
		list.remove(index);
	}

/**
 * remove \count elements starting from index \start<br>
 * */
	public final void remove(int start, int count)
	{
		for(int i = 0; i < count; i++)
			list.remove(start);
	}

/**
 * get \n element from the list<br>
 * probably slow, should not be used much<br>
 * ... but other operations are even slower<br>
 * */
	public final T get(int n)
	{
		return list.get(n);
	}

/**
 * get list size<br>
 * */
	public final int size()
	{
		return list.size();
	}

/**
 * get list iterator for some java fun<br>
 * */
	public final Iterator<T> iterator()
	{
		return list.iterator();
	}

/**
 * get \CAttributeList, containing all attributes with name \name<br>
 * in any list object<br>
 * order of attributes is the same as order of objects<br>
 * */
	public final AttributeList GetAttributes(String name)
		throws ExceptionModelFail
	{
		AttributeList atts = new AttributeList();

		for(T att : list)
		{
			if(att.TestAttribute(name))
				atts.add(att.GetAttribute(name));
		}

		return atts;
	}

/**
 * set the same attribute \name = \value to all list elements<br>
 * */
	public final void SetAttributes(String name, Object value)
		throws ExceptionModelFail, ExceptionDBError
	{
		for(T obj : list)
			obj.SetAttribute(name, value);
	}

/**
 * checks that list contains only one element and returns it<br>
 * if it is not true - throws the exception<br>
 * the function is for use in places where there MUST be only one element,<br>
 * if the model is correct<br>
 * */
	public final T Only()
		throws ExceptionModelFail
	{
		if(size() > 1)
		{
			System.err.println(AutoFormat.PrintNL(this, new AttributeList()));

			throw new ExceptionModelFail("list contains few elements");
		}

		if(size() == 0)
			throw new ExceptionModelFail("list does not contains elements");

		return get(0);
	}

/**
 * allows to set different attributes to all list elements from csv file<br>
 * see {@link CsvFiller} for details
 * @throws ExceptionParseError
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
		List<T> new_list = list.subList(from, to);
		return new_list;
	}

	protected List<T> InnerRanges(int... ranges)
		throws ExceptionModelFail
	{
		if(ranges.length % 2 != 0)
			throw new ExceptionModelFail("even amount of arguments must be provided");

		List<T> new_list = new ArrayList<T>();

		for(int i = 0; i < ranges.length; i += 2)
			new_list.addAll(list.subList(ranges[i], ranges[i+1]));

		return new_list;
	}

	protected List<T> InnerElems(int... elems)
	{
		List<T> new_list = new ArrayList<T>();

		for(int elem : elems)
		{
			new_list.add(list.get(elem));
		}

		return new_list;
	}

	private boolean CheckOp(T obj, String name, Object value, EQueryType type)
	{
		switch(type)
		{
		case EQ:
			return obj.TestAttribute(name) && obj.GetAttribute(name).eq(value);
		case NE:
			return obj.TestAttribute(name) && obj.GetAttribute(name).ne(value);
		case GE:
			return obj.TestAttribute(name) && obj.GetAttribute(name).ge(value);
		case GT:
			return obj.TestAttribute(name) && obj.GetAttribute(name).gt(value);
		case LE:
			return obj.TestAttribute(name) && obj.GetAttribute(name).le(value);
		case LT:
			return obj.TestAttribute(name) && obj.GetAttribute(name).lt(value);
		case NONE:
		case SET:
		default:
			throw new ExceptionModelFail("unsupported operation for list filter: " + type);
		}
	}

	List<T> InnerFilter(String name, Object value, EQueryType type)
		throws ExceptionModelFail
	{
		if(name == null)
			return list;

		if(value == null)
			return InnerFilter(name);

		List<T> new_list = new ArrayList<T>();

		for(T obj : list)
		{
			if(CheckOp(obj, name, value, type))
				new_list.add(obj);
		}

		return new_list;
	}

	public List<T> InnerFilter(String name)
		throws ExceptionModelFail
	{
		if(name == null)
			return list;

		List<T> new_list = new ArrayList<T>();

		for(T obj : list)
		{
			if(obj.TestAttribute(name))
				new_list.add(obj);
		}

		return new_list;
	}

	public List<T> InnerUniq()
	{
		Map<Long, T> map = new LinkedHashMap<Long, T>();

		for (T elem : list)
			map.put(elem.GetUID().getUid(), elem);

		List<T> new_list = new ArrayList<T>();
		new_list.addAll(map.values());

		return new_list;
	}
}
