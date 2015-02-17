/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.core.collections;

import ru.parallel.octotron.core.attributes.IAttribute;
import ru.parallel.octotron.core.attributes.IModelAttribute;
import ru.parallel.octotron.core.attributes.Value;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.primitive.EModelType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.*;

public abstract class ModelList<T extends ModelEntity, R extends ModelList<T, R>> implements Iterable<T>
{
	protected final List<T> list;

	protected ModelList()
	{
		list = new LinkedList<>();
	}

	/**
	 * list reuse
	 * */
	protected ModelList(List<T> list)
	{
		this.list = list;
	}

	/**
	 * list copy
	 * */
	protected ModelList(R list)
	{
		this.list = new LinkedList<>();
		this.list.addAll(list.list);
	}

	public static ModelList<?, ?> Single(ModelEntity entity)
	{
		if(entity.GetType() == EModelType.OBJECT)
		{
			ModelObjectList result = new ModelObjectList();
			result.add((ModelObject)entity);
			return result;
		}
		else if(entity.GetType() == EModelType.OBJECT)
		{
			ModelLinkList result = new ModelLinkList();
			result.add((ModelLink)entity);
			return result;
		}
		else
			throw new ExceptionModelFail("can not create ModelList for: " + entity);
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

	protected boolean CheckOp(T obj, String name, Value value, EQueryType type)
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

	/**
	 * the for loop is inlined here for speed, profiling shows that
	 * CheckOp would be more convenient here, but there is no way to do it pretty and fast
	 * */
	protected List<T> InnerFilter(String name, Value value, EQueryType type)
	{
		if(name == null)
			return list;

		if(!value.IsDefined())
			return InnerFilter(name);

		List<T> new_list = new LinkedList<>();

		switch(type)
		{
			case EQ:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.eq(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case NE:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.ne(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case GE:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.ge(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case GT:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.gt(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case LE:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.le(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case LT:
			{
				for(T obj : list)
				{
					if(!obj.TestAttribute(name))
						continue;

					IModelAttribute attribute = obj.GetAttribute(name);

					if(!attribute.GetValue().IsComputable())
						continue;

					if(attribute.lt(value))
						new_list.add(obj);
				}
				return new_list;
			}
			case NONE:
			default:
				throw new ExceptionModelFail("unsupported operation for list filter: " + type);
		}
/*		for(T obj : list)
		{
			if(CheckOp(obj, name, value, type))
				new_list.add(obj);
		}

		return new_list;*/
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

		for(T elem : list)
			map.put(elem.GetID(), elem);

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

	public final R elems(int... position)
	{
		return Instance(InnerElems(position));
	}

	public final R Filter(String name, Value value, EQueryType type)
	{
		return Instance(InnerFilter(name, value, type));
	}

	public final R Filter(String name, Value value)
	{
		return Instance(InnerFilter(name, value, EQueryType.EQ));
	}

	public final R Filter(String name, Object value, EQueryType type)
	{
		return Filter(name, Value.Construct(value), type);
	}

	public final R Filter(String name, Object value)
	{
		return Filter(name, Value.Construct(value));
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
