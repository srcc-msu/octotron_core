/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import ru.parallel.octotron.primitive.SimpleAttribute;

import java.util.List;

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

@Override
	public SimpleAttributeList AlphabeticSort()
	{
		return new SimpleAttributeList(InnerAlphabeticSort());
	}
}
