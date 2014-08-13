package ru.parallel.octotron.core.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.collections.ObjectList;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class GraphServiceTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphServiceTest.graph = new Neo4jGraph( "dbs/" + GraphServiceTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphServiceTest.graph.Shutdown();
		GraphServiceTest.graph.Delete();
	}

	@After
	public void Clean()
	{
		GraphService.Get().Clean();
	}


	@Test
	public void TestGetAllLinks() throws Exception
	{
		ObjectList<GraphObject, GraphLink> objects = new ObjectList<>();

		final int N = 10;
		for(int i = 0; i < N; i++)
		{
			objects.add(GraphService.Get().AddObject());
		}

		assertEquals(0, GraphService.Get().GetAllLinks().size());

		for(int i = 0; i < N; i++)
		{
			GraphService.Get().AddLink(objects.get(i), objects.get((i * 2) % N), "test");
			assertEquals(i + 1, GraphService.Get().GetAllLinks().size());
		}
	}

	@Test
	public void TestGetAllObjects() throws Exception
	{
		final int N = 10;

		// static must not be visible
		assertEquals(0, GraphService.Get().GetAllObjects().size());

		for(int i = 0; i < N; i++)
		{
			GraphService.Get().AddObject();

			assertEquals(i + 1, GraphService.Get().GetAllObjects().size());
		}
	}

	@Test
	public void TestClean() throws Exception
	{
		GraphObject obj1 = GraphService.Get().AddObject();
		GraphObject obj2 = GraphService.Get().AddObject();
		GraphService.Get().AddObject();
		GraphService.Get().AddLink(obj1, obj2, "test");

		GraphService.Get().Clean();

		assertEquals("unexpected objects found"
			, 0
			, GraphService.Get().GetAllObjects().size());

		assertEquals("some links found"
			, 0
			, GraphService.Get().GetAllLinks().size());
	}
}
