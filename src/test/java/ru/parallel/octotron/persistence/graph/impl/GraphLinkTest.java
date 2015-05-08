package ru.parallel.octotron.persistence.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;

public class GraphLinkTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphLinkTest.graph = new Neo4jGraph( "dbs/" + GraphLinkTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphLinkTest.graph.Shutdown();
		GraphLinkTest.graph.Delete();
	}

	@After
	public void Clean()
	{
		graph_service.Clean();
	}


}