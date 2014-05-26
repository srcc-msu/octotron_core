/*******************************************************************************
 * Copyright (c) 2014 SRCC MSU
 * 
 * Distributed under the MIT License - see the accompanying file LICENSE.txt.
 ******************************************************************************/

package ru.parallel.octotron.neo4j.impl;

import java.util.LinkedList;
import java.util.List;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.*;

import ru.parallel.octotron.core.IIndex;
import ru.parallel.octotron.primitive.EEntityType;
import ru.parallel.octotron.primitive.Uid;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

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

// ------------------------------------------------------------------------------

	private static List<Uid> FromRelIndex(IndexHits<Relationship> it)
	{
		List<Uid> list = new LinkedList<>();

		for(Relationship rel : it)
			list.add(new Uid(rel.getId(), EEntityType.LINK));

		return list;
	}

	private static List<Uid> FromNodeIndex(IndexHits<Node> it)
	{
		List<Uid> list = new LinkedList<>();

		for(Node node : it)
			list.add(new Uid(node.getId(), EEntityType.OBJECT));

		return list;
	}

	@Override
	public List<Uid> GetObjects(String name, Object value)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromNodeIndex(node_auto_index.get(name, value));
	}

	@Override
	public List<Uid> GetLinks(String name, Object value)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromRelIndex(rel_auto_index.get(name, value));
	}

// --------------------------------------------------------------------------

	@Override
	public Uid GetObject(String name, Object value)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		IndexHits<Node> iterator
			= node_auto_index.get(name, value);

		if(!iterator.hasNext())
			throw new ExceptionModelFail
				("element not found " + name + " with value " + value);

		Uid obj_uid = new Uid(iterator.next().getId(), EEntityType.OBJECT);

		if(iterator.hasNext())
			throw new ExceptionModelFail
				("more than one element match the criteria");

		return obj_uid;
	}

	@Override
	public Uid GetLink(String name, Object value)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		IndexHits<Relationship> iterator
			= rel_auto_index.get(name, value);

		if(!iterator.hasNext())
			throw new ExceptionModelFail
				("element not found" + name);

		Uid link_uid = new Uid(iterator.next().getId(), EEntityType.LINK);

		if(iterator.hasNext())
			throw new ExceptionModelFail
				("more than one element match the criteria");

		return link_uid;
	}

// -----------------------------------------------------------------------------

	@Override
	public List<Uid> QueryObjects(String name, String pattern)
	{
		ReadableIndex<Node> node_auto_index = graph.GetInnerIndex()
			.getNodeAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromNodeIndex(node_auto_index.query(name, pattern));
	}

	@Override
	public List<Uid> QueryLinks(String name, String pattern)
	{
		ReadableIndex<Relationship> rel_auto_index = graph.GetInnerIndex()
			.getRelationshipAutoIndexer().getAutoIndex();

		return Neo4jIndex.FromRelIndex(rel_auto_index.query(name, pattern));
	}

// -----------------------------------------------------------------------------

	@Override
	public List<Uid> GetObjects(String name)
	{
		return QueryObjects(name, "*");
	}

	@Override
	public List<Uid> GetLinks(String name)
	{
		return QueryLinks(name, "*");
	}

}
