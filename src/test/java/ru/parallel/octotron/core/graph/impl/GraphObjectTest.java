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

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphObjectTest.graph = new Neo4jGraph( "dbs/" + GraphObjectTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);
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
		GraphService.Get().Clean();
	}

	@Test
	public void TestGetInLinks() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(0, object2.GetInLinks().size());

		GraphLink link1 = GraphService.Get().AddLink(object1, object2, "test");

		assertEquals(link1.GetID()
			, object2.GetInLinks().iterator().next().GetID());

		GraphLink link2 = GraphService.Get().AddLink(object2, object1, "test");

		assertEquals(link2.GetID()
			, object1.GetInLinks().iterator().next().GetID());
	}

	@Test
	public void TestGetOutLink() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(0, object2.GetInLinks().size());

		GraphLink link1 = GraphService.Get().AddLink(object1, object2, "test");

		assertEquals(link1.GetID()
			, object1.GetOutLinks().iterator().next().GetID());

		GraphLink link2 = GraphService.Get().AddLink(object2, object1, "test");

		assertEquals(link2.GetID()
			, object2.GetOutLinks().iterator().next().GetID());
	}
}