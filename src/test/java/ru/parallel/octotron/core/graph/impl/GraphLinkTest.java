package ru.parallel.octotron.core.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public class GraphLinkTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphLinkTest.graph = new Neo4jGraph( "dbs/" + GraphLinkTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
GraphService.Init(graph);
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
		GraphService.Get().Clean();
	}


}