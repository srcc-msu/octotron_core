/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.graph.collections;

import ru.parallel.octotron.core.graph.IEntity;
import ru.parallel.octotron.core.primitive.SimpleAttribute;

import java.util.List;

/**
 * implements list container for entities<br>
 * allows to filter entities basing on attributes
 * and obtain list of attributes<br>
 * */
public class EntityList<T extends IEntity> extends IEntityList<T>
{
	protected EntityList(List<T> list)
	{
		super(list);
	}

	public EntityList(EntityList<T> list)
	{
		super(list);
	}

	public EntityList()
	{
		super();
	}

	public EntityList<T> append(EntityList<T> list)
	{
		return new EntityList<>(InnerAppend(list.list));
	}

	@Override
	public EntityList<T> range(int from, int to)
	{
		return new EntityList<>(InnerRange(from, to));
	}

	@Override
	public EntityList<T> ranges(int... ranges)
	{
		return new EntityList<>(InnerRanges(ranges));
	}

	@Override
	public EntityList<T> Filter(SimpleAttribute att, EQueryType type)
	{
		return new EntityList<>(InnerFilter(att.GetName(), att.GetValue(), type));
	}

	@Override
	public EntityList<T> Filter(String name, Object value, EQueryType type)
	{
		return new EntityList<>(InnerFilter(name, value, type));
	}

	@Override
	public EntityList<T> Filter(SimpleAttribute att)
	{
		return new EntityList<>(InnerFilter(att.GetName(), att.GetValue(), EQueryType.EQ));
	}

	@Override
	public EntityList<T> Filter(String name, Object value)
	{
		return new EntityList<>(InnerFilter(name, value, EQueryType.EQ));
	}

	@Override
	public EntityList<T> Filter(String name)
	{
		return new EntityList<>(InnerFilter(name));
	}

	@Override
	public EntityList<T> Uniq()
	{
		return new EntityList<>(InnerUniq());
	}
}
