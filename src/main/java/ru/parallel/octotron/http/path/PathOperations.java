/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http.path;

import ru.parallel.octotron.core.attributes.impl.Value;
import ru.parallel.octotron.core.collections.ModelLinkList;
import ru.parallel.octotron.core.collections.ModelList;
import ru.parallel.octotron.core.collections.ModelObjectList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.services.ServiceLocator;

import java.util.List;

/**
 * contains all operations, that can be invoked via request<br>
 * */
public abstract class PathOperations
{
	public static class Query
	{
		public final String name;
		public final Value operand;
		public final ModelList.EQueryType type;

		public Query(String name, Value operand, ModelList.EQueryType type)
		{
			this.name = name;
			this.operand = operand;
			this.type = type;
		}
	}

	enum CHAIN_TYPE
	{
		E_START, E_OBJ_LIST, E_LINK_LIST, E_MATCH, E_ANY
	}

	private interface ITransform
	{
		ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params);
	}

	/**
 * contains information about tokens: types and transformation<br>
 * can be constructed from another token with additional params<br>
 * */
	public static class PathToken
	{
		private final String name;

		private final CHAIN_TYPE in;
		private final CHAIN_TYPE out;

		private final ITransform transform;
		private List<Query> params = null;

		public final String GetName()
		{
			return name;
		}

		public final CHAIN_TYPE GetIn()
		{
			return in;
		}

		public final CHAIN_TYPE GetOut()
		{
			return out;
		}

		public ModelList<? extends ModelEntity, ?> Transform(Object obj)
		{
			return transform.Transform(obj, params);
		}

		private PathToken(String name, CHAIN_TYPE in, CHAIN_TYPE out
			, ITransform transform)
		{
			this.name = name;
			this.in = in;
			this.out = out;
			this.transform = transform;
		}

		public PathToken(PathToken token, List<Query> params) {
			this.name = token.name;
			this.in = token.in;
			this.out = token.out;
			this.transform = token.transform;

			this.params = params;
		}
	}

	@SuppressWarnings("unchecked")
	private static ModelObjectList ToObjList(Object obj)
	{
		if(obj instanceof ModelObjectList)
			return (ModelObjectList)obj;
		else
			throw new ExceptionModelFail("operation requires an object list");
	}

	@SuppressWarnings("unchecked")
	private static ModelLinkList ToLinkList(Object obj)
	{
		if(obj instanceof ModelLinkList)
			return (ModelLinkList)obj;
		else
			throw new ExceptionModelFail("operation requires a link list");
	}

/**
 * filter the list according to request params<br>
 * */
	public static final PathToken q = new PathToken("q", CHAIN_TYPE.E_MATCH
		, CHAIN_TYPE.E_MATCH, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
			{
			if(params.size() != 1) // TODO
				throw new ExceptionModelFail("query accepts only one filter");

			if(obj instanceof ModelObjectList)
			{
				return ToObjList(obj)
					.Filter(params.get(0).name, params.get(0).operand, params.get(0).type);
			}
			else if(obj instanceof ModelLinkList)
			{
				return ToLinkList(obj)
					.Filter(params.get(0).name, params.get(0).operand, params.get(0).type);
			}

			throw new ExceptionModelFail(
				"internal error: operation q is not applicable to " + obj);
		}
	});

/**
 * Retrieve an object list from DB index,<br>
 * basing on request params<br>
 * */
	public static final PathToken obj = new PathToken("obj", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
			{
			if(params.size() != 1)
				throw new ExceptionModelFail
					("index operation must be querying a single indexed value");

			if(params.get(0).operand != null)
				return ServiceLocator.INSTANCE.GetModelService().GetModelData()
					.GetObjects(params.get(0).name, params.get(0).operand);
			else
				return ServiceLocator.INSTANCE.GetModelService().GetModelData()
					.GetObjects(params.get(0).name);
		}
	});

/**
 * Retrieve a link list from DB index,<br>
 * basing on request params<br>
 * */
	public static final PathToken link = new PathToken("link", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
			{
			if(params.size() != 1)
				throw new ExceptionModelFail
					("index operation must be querying a single indexed value");

			if(params.get(0).operand != null)
				return ServiceLocator.INSTANCE.GetModelService().GetModelData()
					.GetLinks(params.get(0).name, params.get(0).operand);
			else
				return ServiceLocator.INSTANCE.GetModelService().GetModelData()
					.GetLinks(params.get(0).name);
		}
	});

	/**
	 * removes duplicated elements from the list<br>
	 * */
	public static final PathToken uniq = new PathToken("uniq", CHAIN_TYPE.E_MATCH
		, CHAIN_TYPE.E_MATCH, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(obj instanceof ModelObjectList)
				return ToObjList(obj).Uniq();
			else if(obj instanceof ModelLinkList)
				return ToLinkList(obj).Uniq();

			throw new ExceptionModelFail(
				"internal error: operation uniq is not applicable to " + obj);
		}
	});
	/**
	 * removes duplicated elements from the list<br>
	 * */
	public static final PathToken self = new PathToken("self", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_ANY, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(obj instanceof ModelObjectList)
				return ToObjList(obj).Uniq();
			else if(obj instanceof ModelLinkList)
				return ToLinkList(obj).Uniq();

			throw new ExceptionModelFail(
				"internal error: operation self is not applicable to " + obj);
		}
	});

/**
 * returns a list of all neighbors with in link to current objects<br>
 * */
	public static final PathToken in_n = new PathToken("in_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(params.size() > 1)
				throw new ExceptionModelFail("in_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetInNeighbors(params.get(0).name, params.get(0).operand);

			return ToObjList(obj).GetInNeighbors();
		}
	});

/**
 * returns a list of all neighbors with out link to current objects<br>
 * */
	public static final PathToken out_n = new PathToken("out_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(params.size() > 1)
				throw new ExceptionModelFail("out_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetOutNeighbors(params.get(0).name, params.get(0).operand);

			return ToObjList(obj).GetOutNeighbors();
		}
	});

/**
 * returns a list of all neighbors with out link to current objects<br>
 * */
	public static final PathToken u_n = new PathToken("u_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(params.size() > 1)
				throw new ExceptionModelFail("u_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetUndirectedNeighbors(params.get(0).name, params.get(0).operand);

			return ToObjList(obj).GetUndirectedNeighbors();
		}
	});

/**
 * returns a list of all neighbors to current objects<br>
 * */
	public static final PathToken all_n = new PathToken("all_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			if(params.size() > 1)
				throw new ExceptionModelFail("all_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetAllNeighbors(params.get(0).name, params.get(0).operand);

			return ToObjList(obj).GetOutNeighbors()
				.append(ToObjList(obj).GetAllNeighbors());
		}
	});

/**
 * return all incoming links for the given list objects<br>
 * */
	public static final PathToken in_l = new PathToken("in_l", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToObjList(obj).GetInLinks();
		}
	});

/**
 * return all outgoing links for the given objects<br>
 * */
	public static final PathToken out_l = new PathToken("out_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToObjList(obj).GetOutLinks();
		}
	});

/**
 * return all links for the given objects<br>
 * */
	public static final PathToken u_l = new PathToken("u_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToObjList(obj).GetUndirectedLinks();
		}
	});

/**
 * return all links for the given objects<br>
 * */
	public static final PathToken all_l = new PathToken("all_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToObjList(obj).GetAllLinks();
		}
	});

/**
 * return list of source objects for all given links<br>
 * */
	public static final PathToken source = new PathToken("source", CHAIN_TYPE.E_LINK_LIST, CHAIN_TYPE.E_OBJ_LIST
		, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToLinkList(obj).Source();
		}
	});

/**
 * return list of target objects for all given links<br>
 * */
	public static final PathToken target = new PathToken("target", CHAIN_TYPE.E_LINK_LIST, CHAIN_TYPE.E_OBJ_LIST
		, new ITransform()
	{
		@Override
		public ModelList<? extends ModelEntity, ?> Transform(Object obj, List<Query> params)
		{
			return ToLinkList(obj).Target();
		}
	});
}
