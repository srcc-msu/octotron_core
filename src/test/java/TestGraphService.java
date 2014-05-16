package test.java;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;

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
			graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		graph.Shutdown();
		try
		{
			graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			fail(e.getMessage());
		}
	}
}
