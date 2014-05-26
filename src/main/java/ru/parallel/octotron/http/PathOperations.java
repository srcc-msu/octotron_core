/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.http;

import java.util.List;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.logic.ExecutionController;
import ru.parallel.octotron.primitive.SimpleAttribute;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.primitive.exception.ExceptionParseError;
import ru.parallel.octotron.utils.IEntityList;
import ru.parallel.octotron.utils.OctoLinkList;
import ru.parallel.octotron.utils.OctoObjectList;

import org.apache.commons.lang3.tuple.Pair;

/**
 * contains all operations, that can be invoked via request<br>
 * */
public abstract class PathOperations
{
	enum CHAIN_TYPE
	{
		E_START, E_OBJ_LIST, E_LINK_LIST, E_MATCH, E_ANY
	}

	private interface ITransform
	{
		IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
				, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
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
		private List<Pair<SimpleAttribute, IEntityList.EQueryType>> params = null;

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

		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj)
				throws ExceptionParseError
		{
			return transform.Transform(graph_service, exec_control, obj, params);
		}

		private PathToken(String name, CHAIN_TYPE in, CHAIN_TYPE out
			, ITransform transform)
		{
			this.name = name;
			this.in = in;
			this.out = out;
			this.transform = transform;
		}

		public PathToken(PathToken token, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params) {
			this.name = token.name;
			this.in = token.in;
			this.out = token.out;
			this.transform = token.transform;

			this.params = params;
		}
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
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
				throws ExceptionParseError
		{
			if(params.size() != 1) // TODO
				throw new ExceptionParseError("query accepts only one filter");

			if(obj instanceof OctoObjectList)
			{
				OctoObjectList list = (OctoObjectList) obj;
				return list.Filter(params.get(0).getLeft(), params.get(0).getRight());
			}
			else if(obj instanceof OctoLinkList)
			{
				OctoLinkList list = (OctoLinkList) obj;
				return list.Filter(params.get(0).getLeft(), params.get(0).getRight());
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
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
				throws ExceptionParseError
		{
			if(params.size() != 1)
				throw new ExceptionParseError
					("index operation must be quering a single indexed value");

			SimpleAttribute attr = params.get(0).getLeft();

			if(attr.GetValue() != null)
				return graph_service.GetObjects(attr);
			else
				return graph_service.GetObjects(attr.GetName());
		}
	});

/**
 * Retrieve a link list from DBindex,<br>
 * basing on request params<br>
 * */
	public static final PathToken link = new PathToken("link", CHAIN_TYPE.E_START
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
				throws ExceptionParseError
		{
			if(params.size() != 1)
				throw new ExceptionParseError
					("index operation must be quering a single indexed value");

			SimpleAttribute attr = params.get(0).getLeft();

			if(attr.GetValue() != null)
				return graph_service.GetLinks(attr);
			else
				return graph_service.GetLinks(attr.GetName());
		}
	});

/**
 * removes duplicated elements from the list<br>
 * */
	public static final PathToken uniq = new PathToken("uniq", CHAIN_TYPE.E_MATCH
		, CHAIN_TYPE.E_MATCH, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
			throws ExceptionParseError
		{
			if(obj instanceof OctoObjectList)
				return ((OctoObjectList) obj).Uniq();
			else if(obj instanceof OctoLinkList)
				return ((OctoLinkList) obj).Uniq();

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
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
			throws ExceptionParseError, ExceptionModelFail
		{
			if(params.size() > 1)
				throw new ExceptionParseError("in_n accepts only one or zero params");

			if(params.size() == 1)
				return ((OctoObjectList)obj).GetInNeighbors(params.get(0).getLeft());

			return ((OctoObjectList)obj).GetInNeighbors();
		}
	});

/**
 * returns a list of all neighbors with out link to current objects<br>
 * */
	public static final PathToken out_n = new PathToken("out_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
			throws ExceptionParseError, ExceptionModelFail
		{
			if(params.size() > 1)
				throw new ExceptionParseError("out_n accepts only one or zero params");

			if(params.size() == 1)
				return ((OctoObjectList)obj).GetOutNeighbors(params.get(0).getLeft());

			return ((OctoObjectList)obj).GetOutNeighbors();
		}
	});

/**
 * returns a list of all neighbors to current objects<br>
 * */
	public static final PathToken all_n = new PathToken("all_n", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_OBJ_LIST, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
			throws ExceptionParseError, ExceptionModelFail
		{
			if(params.size() > 1)
				throw new ExceptionParseError("all_n accepts only one or zero params");

			if(params.size() == 1)
				return ((OctoObjectList)obj).GetOutNeighbors(params.get(0).getLeft())
					.append(((OctoObjectList)obj).GetOutNeighbors(params.get(0).getLeft()));

			return ((OctoObjectList)obj).GetOutNeighbors()
				.append(((OctoObjectList)obj).GetOutNeighbors());
		}
	});

/**
 * return all incoming links for the given list objects<br>
 * */
	public static final PathToken in_l = new PathToken("in_l", CHAIN_TYPE.E_OBJ_LIST
		, CHAIN_TYPE.E_LINK_LIST, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
		{
			return ((OctoObjectList)obj).GetInLinks();
		}
	});

/**
 * return all outgoing links for the given objects<br>
 * */
	public static final PathToken out_l = new PathToken("out_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
		{
			return ((OctoObjectList)obj).GetOutLinks();
		}
	});

/**
 * return all links for the given objects<br>
 * */
	public static final PathToken all_l = new PathToken("all_l", CHAIN_TYPE.E_OBJ_LIST, CHAIN_TYPE.E_LINK_LIST
		, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
		{
			return ((OctoObjectList)obj).GetInLinks().append(((OctoObjectList)obj).GetOutLinks());
		}
	});

/**
 * return list of source objects for all given links<br>
 * */
	public static final PathToken source = new PathToken("source", CHAIN_TYPE.E_LINK_LIST, CHAIN_TYPE.E_OBJ_LIST
		, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
		{
			return ((OctoLinkList)obj).Source();
		}
	});

/**
 * return list of target objects for all given links<br>
 * */
	public static final PathToken target = new PathToken("target", CHAIN_TYPE.E_LINK_LIST, CHAIN_TYPE.E_OBJ_LIST
		, new ITransform()
	{
		public IEntityList<?> Transform(GraphService graph_service, ExecutionController exec_control
			, Object obj, List<Pair<SimpleAttribute, IEntityList.EQueryType>> params)
		{
			return ((OctoLinkList)obj).Target();
		}
	});
}
