/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.utils;

import ru.parallel.octotron.core.OctoEntity;
import ru.parallel.octotron.primitive.SimpleAttribute;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * */
public class OctoEntityList extends IEntityList<OctoEntity>
{
	private OctoEntityList(List<OctoEntity> list)
	{
		super(list);
	}

	public OctoEntityList(OctoEntityList list)
	{
		super(list);
	}

	public OctoEntityList()
	{
		super();
	}

	public OctoEntityList append(IEntityList list)
	{
		return new OctoEntityList(InnerAppend(list.list));
	}

	public OctoEntityList range(int from, int to)
	{
		return new OctoEntityList(InnerRange(from, to));
	}

	public OctoEntityList ranges(int... ranges)
	{
		return new OctoEntityList(InnerRanges(ranges));
	}

	public OctoEntityList Filter(SimpleAttribute att, EQueryType type)
	{
		return new OctoEntityList(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	public OctoEntityList Filter(String name, Object value, EQueryType type)
	{
		return new OctoEntityList(InnerFilter(name, value, type));
	}

	public OctoEntityList Filter(SimpleAttribute att)
	{
		return new OctoEntityList(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	public OctoEntityList Filter(String name, Object value)
	{
		return new OctoEntityList(InnerFilter(name, value, EQueryType.EQ));
	}

	public OctoEntityList Filter(String name)
	{
		return new OctoEntityList(InnerFilter(name));
	}

	/**
	 * returns all surrounding links/objects</br>
	 * */
	public OctoEntityList GetSurround()
	{
		OctoEntityList result = new OctoEntityList();

		for(OctoEntity entity : list)
			result = result.append(entity.GetSurround());

		return result.Uniq();
	}

	public OctoEntityList Uniq()
	{
		return new OctoEntityList(InnerUniq());
	}
}
