package ru.parallel.octotron.core.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class GraphObjectTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphObjectTest.graph = new Neo4jGraph( "dbs/" + GraphObjectTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphObjectTest.graph.Shutdown();
		GraphObjectTest.graph.Delete();
	}

	@After
	public void Clean()
	{
		graph_service.Clean();
	}

	@Test
	public void TestGetInLinks() throws Exception
	{
		GraphObject object1 = graph_service.AddObject();
		GraphObject object2 = graph_service.AddObject();

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(0, object2.GetInLinks().size());

		GraphLink link1 = graph_service.AddLink(object1, object2, "test");

		assertEquals(link1.GetID()
			, object2.GetInLinks().iterator().next().GetID());

		GraphLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetID()
			, object1.GetInLinks().iterator().next().GetID());
	}

	@Test
	public void TestGetOutLink() throws Exception
	{
		GraphObject object1 = graph_service.AddObject();
		GraphObject object2 = graph_service.AddObject();

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(0, object2.GetInLinks().size());

		GraphLink link1 = graph_service.AddLink(object1, object2, "test");

		assertEquals(link1.GetID()
			, object1.GetOutLinks().iterator().next().GetID());

		GraphLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetID()
			, object2.GetOutLinks().iterator().next().GetID());
	}
}