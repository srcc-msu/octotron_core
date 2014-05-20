package ru.parallel.octotron.core;

import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public class TestGraphService extends Assert
{
	static GraphService graph_service;
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			TestGraphService.graph = new Neo4jGraph("dbs/test_graph_service", Neo4jGraph.Op.RECREATE);
			TestGraphService.graph_service = new GraphService(TestGraphService.graph);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		TestGraphService.graph.Shutdown();
		try
		{
			TestGraphService.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}


}
