package ru.parallel.octotron.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class GraphObjectTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphObjectTest.graph = new Neo4jGraph( "dbs/" + GraphObjectTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphObjectTest.graph_service = new GraphService(GraphObjectTest.graph);
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

		assertEquals(link1.GetUID().getUid()
			, object2.GetInLinks().Only().GetUID().getUid());

		GraphLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetUID().getUid()
			, object1.GetInLinks().Only().GetUID().getUid());
	}

	@Test
	public void TestGetOutLink() throws Exception
	{
		GraphObject object1 = graph_service.AddObject();
		GraphObject object2 = graph_service.AddObject();

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(0, object2.GetInLinks().size());

		GraphLink link1 = graph_service.AddLink(object1, object2, "test");

		assertEquals(link1.GetUID().getUid()
			, object1.GetOutLinks().Only().GetUID().getUid());

		GraphLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetUID().getUid()
			, object2.GetOutLinks().Only().GetUID().getUid());
	}
}