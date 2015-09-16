/*******************************************************************************
 * Copyright (c) 2014-2015 SRCC MSU
 *
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.persistence.neo4j;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import ru.parallel.octotron.core.primitive.Info;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.persistence.graph.EGraphType;
import ru.parallel.octotron.persistence.graph.IIndex;

import java.util.LinkedList;
import java.util.List;

/**
 * provides access to index for searching elements in the graph
 * currently allows to use any attributes,
 * but works only with /String attributes
 * */
public class Neo4jIndex implements IIndex
{
	private final Neo4jGraph graph;

	public Neo4jIndex(Neo4jGraph graph)
	{
		this.graph = graph;
	}

	@Override
	public void EnableObjectIndex(String name)
	{
		AutoIndexer<Node> node_auto_indexer =
			graph.GetInnerIndex().getNodeAutoIndexer();

		node_auto_indexer.startAutoIndexingProperty(name);
		node_auto_indexer.setEnabled(true);
	}

	@Override
	public void DisableObjectIndex(String name)
	{
		AutoIndexer<Node> node_auto_indexer =
			graph.GetInnerIndex().getNodeAutoIndexer();

		node_auto_indexer.stopAutoIndexingProperty(name);
	}

	@Override
	public void EnableLinkIndex(String name)
	{
		AutoIndexer<Relationship> rel_auto_indexer =
			graph.GetInnerIndex().getRelationshipAutoIndexer();

		rel_auto_indexer.startAutoIndexingProperty(name);
		rel_auto_indexer.setEnabled(true);
	}

	@Override
	public void DisableLinkIndex(String name)
	{
		AutoIndexer<Relationship> rel_auto_indexer =
			graph.GetInnerIndex().getRelationshipAutoIndexer();

		rel_auto_indexer.stopAutoIndexingProperty(name);
	}

//--------

	private static List<Info<EGraphType>> FromRelIndex(IndexHits<Relationship> it)
	{
		List<Info<EGraphType>> list = new LinkedList<>();

		for(Relationship rel : it)
			list.add(new Info<>(rel.getId(), EGraphType.LINK));

		return list;
	}

	private static List<Info<EGraphType>> FromNodeIndex(IndexHits<Node> it)
	{
		List<Info<EGraphType>> list = new LinkedList<>();

		for(Node node : it)
			list.add(new Info<>(node.getId(), EGraphType.OBJECT));

		return list;
	}

	@Override
	public List<Info<EGraphType>> GetObjects(String name, Object value)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromNodeIndex(node_auto_index.get(name, value));
	}

	@Override
	public List<Info<EGraphType>> GetLinks(String name, Object value)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromRelIndex(rel_auto_index.get(name, value));
	}

//--------

	@Override
	public Info<EGraphType> GetObject(String name, Object value)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		IndexHits<Node> iterator
			= node_auto_index.get(name, value);

		if(!iterator.hasNext())
			throw new ExceptionModelFail
				("element not found " + name + " with value " + value);

		Info<EGraphType> obj_uid = new Info<>(iterator.next().getId(), EGraphType.OBJECT);

		if(iterator.hasNext())
			throw new ExceptionModelFail
				("more than one element match the criteria: " + name + " " + value.toString());

		return obj_uid;
	}

	@Override
	public Info<EGraphType> GetLink(String name, Object value)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		IndexHits<Relationship> iterator
			= rel_auto_index.get(name, value);

		if(!iterator.hasNext())
			throw new ExceptionModelFail
				("element not found" + name);

		Info<EGraphType> link_uid = new Info<>(iterator.next().getId(), EGraphType.LINK);

		if(iterator.hasNext())
			throw new ExceptionModelFail
				("more than one element match the criteria: " + name + " " + value.toString());

		return link_uid;
	}

//--------

	@Override
	public List<Info<EGraphType>> QueryObjects(String name, String pattern)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromNodeIndex(node_auto_index.query(name, pattern));
	}

	@Override
	public List<Info<EGraphType>> QueryLinks(String name, String pattern)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromRelIndex(rel_auto_index.query(name, pattern));
	}

//--------

	@Override
	public List<Info<EGraphType>> GetObjects(String name)
	{
		return QueryObjects(name, "*");
	}

	@Override
	public List<Info<EGraphType>> GetLinks(String name)
	{
		return QueryLinks(name, "*");
	}

}