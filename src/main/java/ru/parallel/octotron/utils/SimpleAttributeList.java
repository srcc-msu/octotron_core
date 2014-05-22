/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import java.util.LinkedList;
import java.util.List;

import ru.parallel.octotron.primitive.SimpleAttribute;

public class SimpleAttributeList extends IAttributeList<SimpleAttribute>
{
	public SimpleAttributeList()
	{
		super();
	}

	private SimpleAttributeList(List<SimpleAttribute> list)
	{
		super(list);
	}

	public SimpleAttributeList(SimpleAttributeList list)
	{
		super(list);
	}

	public SimpleAttributeList append(SimpleAttributeList list2)
	{
		return new SimpleAttributeList(InnerAppend(list2.list));
	}

@Override
	public SimpleAttributeList AlphabeticSort()
	{
		return new SimpleAttributeList(InnerAlphabeticSort());
	}
}
