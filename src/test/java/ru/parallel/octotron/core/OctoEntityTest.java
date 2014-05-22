package ru.parallel.octotron.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;

public class OctoEntityTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		OctoEntityTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
		OctoEntityTest.graph_service = new GraphService(OctoEntityTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		OctoEntityTest.graph.Shutdown();
		OctoEntityTest.graph.Delete();
	}

	@Test
	public void TestDeclareAttribute() throws Exception
	{
		OctoObject object = graph_service.AddObject();
		object.DeclareAttribute("test", "");

		boolean catched = false;

		try
		{
			object.DeclareAttribute("test", "");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true;
		}
		Assert.assertTrue(catched);
	}
}