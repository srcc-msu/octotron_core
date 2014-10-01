/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import ru.parallel.octotron.core.graph.collections.EntityList;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.model.collections.ModelLinkList;
import ru.parallel.octotron.core.model.collections.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.core.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.logic.ExecutionController;

import java.util.List;

/**
 * contains all operations, that can be invoked via request<br>
 * */
public abstract class PathOperations
{
	public static class Query
	{
		public SimpleAttribute attribute;
		public EntityList.EQueryType type;

		public Query(SimpleAttribute attribute, EntityList.EQueryType type)
		{
			this.attribute = attribute;
			this.type = type;
		}
	}

	enum CHAIN_TYPE
	{
		E_START, E_OBJ_LIST, E_LINK_LIST, E_MATCH, E_ANY
	}

	private interface ITransform
	{
		EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
				, Object obj, List<Query> params)
				throws ExceptionParseError;
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

		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj)
				throws ExceptionParseError
		{
			return transform.Transform(exec_control, obj, params);
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
 * next tokens describe all transformation, allowed in request<br>
 * all requests are described in redmine<br>
 * */

/**
 * filter the list according to request params<br>
 * */
	public static final PathToken q = new PathToken("q", CHAIN_TYPE.E_MATCH
		, CHAIN_TYPE.E_MATCH, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
				throws ExceptionParseError
		{
			if(params.size() != 1) // TODO
				throw new ExceptionParseError("query accepts only one filter");

			if(obj instanceof ModelObjectList)
			{
				ModelObjectList list = ToObjList(obj);
				return list.Filter(params.get(0).attribute, params.get(0).type);
			}
			else if(obj instanceof ModelLinkList)
			{
				ModelLinkList list = ToLinkList(obj);
				return list.Filter(params.get(0).attribute, params.get(0).type);
			}

			throw new ExceptionParseError(
				"internal error: operation q is not applicable to " + obj);
		}
	});

/**
 * Retrieve an object list from DBindex,<br>
 * basing on request params<br>
 * */
	public static final PathToken obj = new PathToken("obj", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
				throws ExceptionParseError
		{
			if(params.size() != 1)
				throw new ExceptionParseError
					("index operation must be querying a single indexed value");

			SimpleAttribute attr = params.get(0).attribute;

			if(attr.GetValue() != null)
				return ModelService.GetObjects(attr);
			else
				return ModelService.GetObjects(attr.GetName());
		}
	});

/**
 * Retrieve a link list from DBindex,<br>
 * basing on request params<br>
 * */
	public static final PathToken link = new PathToken("link", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
				throws ExceptionParseError
		{
			if(params.size() != 1)
				throw new ExceptionParseError
					("index operation must be querying a single indexed value");

			SimpleAttribute attr = params.get(0).attribute;

			if(attr.GetValue() != null)
				return ModelService.GetLinks(attr);
			else
				return ModelService.GetLinks(attr.GetName());
		}
	});

/**
 * removes duplicated elements from the list<br>
 * */
	public static final PathToken uniq = new PathToken("uniq", CHAIN_TYPE.E_MATCH
		, CHAIN_TYPE.E_MATCH, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
			throws ExceptionParseError
		{
			if(obj instanceof ModelObjectList)
				return ToObjList(obj).Uniq();
			else if(obj instanceof ModelLinkList)
				return ToLinkList(obj).Uniq();

			throw new ExceptionParseError(
				"internal error: operation uniq is not applicable to " + obj);
		}
	});

/**
 * returns a list of all neighbors with in link to current objects<br>
 * */
	public static final PathToken in_n = new PathToken("in_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
			throws ExceptionParseError
		{
			if(params.size() > 1)
				throw new ExceptionParseError("in_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetInNeighbors(params.get(0).attribute);

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
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
			throws ExceptionParseError
		{
			if(params.size() > 1)
				throw new ExceptionParseError("out_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetOutNeighbors(params.get(0).attribute);

			return ToObjList(obj).GetOutNeighbors();
		}
	});

/**
 * returns a list of all neighbors to current objects<br>
 * */
	public static final PathToken all_n = new PathToken("all_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
			throws ExceptionParseError
		{
			if(params.size() > 1)
				throw new ExceptionParseError("all_n accepts only one or zero params");

			if(params.size() == 1)
				return ToObjList(obj).GetOutNeighbors(params.get(0).attribute)
					.append(ToObjList(obj).GetOutNeighbors(params.get(0).attribute));

			return ToObjList(obj).GetOutNeighbors()
				.append(ToObjList(obj).GetOutNeighbors());
		}
	});

/**
 * return all incoming links for the given list objects<br>
 * */
	public static final PathToken in_l = new PathToken("in_l", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
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
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
		{
			return ToObjList(obj).GetOutLinks();
		}
	});

/**
 * return all links for the given objects<br>
 * */
	public static final PathToken all_l = new PathToken("all_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control, Object obj, List<Query> params)
		{
			return ToObjList(obj).GetInLinks().append(ToObjList(obj).GetOutLinks());
		}
	});

/**
 * return list of source objects for all given links<br>
 * */
	public static final PathToken source = new PathToken("source", CHAIN_TYPE.E_LINK_LIST, CHAIN_TYPE.E_OBJ_LIST
		, new ITransform()
	{
		@Override
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
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
		public EntityList<? extends ModelEntity, ?> Transform(ExecutionController exec_control
			, Object obj, List<Query> params)
		{
			return ToLinkList(obj).Target();
		}
	});
}
