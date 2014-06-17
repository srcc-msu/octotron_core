/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import ru.parallel.octotron.core.OctoAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * implements list container for attributes<br>
 * and provides some additional functionality, like filtering<br>
 * only filtering for now, to be honest..<br>
 * */
public class OctoAttributeList extends IAttributeList<OctoAttribute>
{
	public OctoAttributeList()
	{
		super();
	}

	private OctoAttributeList(List<OctoAttribute> list)
	{
		super(list);
	}

	public OctoAttributeList(OctoAttributeList list)
	{
		super(list);
	}

	public OctoAttributeList ge(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.ge(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList le(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.le(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList gt(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.gt(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList lt(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.lt(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList eq(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.eq(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

	public OctoAttributeList ne(Object val)
	{
		List<OctoAttribute> new_list = new ArrayList<>();

		for(OctoAttribute att : list)
			if(att.ne(val))
				new_list.add(att);

		return new OctoAttributeList(new_list);
	}

@Override
	public OctoAttributeList AlphabeticSort()
	{
		return new OctoAttributeList(InnerAlphabeticSort());
	}
}

