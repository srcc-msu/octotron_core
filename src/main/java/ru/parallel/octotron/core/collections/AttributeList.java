/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.attributes.IAttribute;
import ru.parallel.octotron.core.attributes.Value;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

@SuppressWarnings("serial")
public final class AttributeList<T extends IAttribute> extends LinkedList<T>
{
	public AttributeList() {}

	public AttributeList(AttributeList<T> list)
	{
		this();
		addAll(list);
	}

	/**
	 * creates a new list, that contains all elements from this and /list2<br>
	 * */
	public final AttributeList<T> append(AttributeList<? extends T> list2)
	{
		AttributeList<T> new_list = new AttributeList<>();

		new_list.addAll(this);
		new_list.addAll(list2);

		return new_list;
	}

	/**
	 * returns a new list, that has attributes sorted in alphabetic order by keys<br>
	 * */
	public final AttributeList<T> AlphabeticSort()
	{
		AttributeList<T> new_list = new AttributeList<>(this);

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

	public AttributeList<T> ge(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.ge(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> le(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.le(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> gt(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.gt(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> lt(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.lt(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> eq(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.eq(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> ne(Value val)
	{
		AttributeList<T> new_list = new AttributeList<>();

		for(T att : this)
			if(att.GetValue().IsComputable() && att.ne(val))
				new_list.add(att);

		return new_list;
	}

	public AttributeList<T> ge(Object value)
	{
		return ge(Value.Construct(value));
	}

	public AttributeList<T> le(Object value)
	{
		return le(Value.Construct(value));
	}

	public AttributeList<T> gt(Object value)
	{
		return gt(Value.Construct(value));
	}

	public AttributeList<T> lt(Object value)
	{
		return lt(Value.Construct(value));
	}

	public AttributeList<T> eq(Object value)
	{
		return eq(Value.Construct(value));
	}

	public AttributeList<T> ne(Object value)
	{
		return ne(Value.Construct(value));
	}

// ---

	private void writeObject(ObjectOutputStream stream)
		throws IOException
	{
		throw new IOException("NIY");
	}
}
