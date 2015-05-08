package ru.parallel.octotron.persistence.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.persistence.neo4j.Neo4jGraph;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphServiceTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphServiceTest.graph = new Neo4jGraph( "dbs/" + GraphServiceTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		graph_service = new GraphService(graph);
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
		graph_service.Clean();
	}


	@Test
	public void TestGetAllLinks() throws Exception
	{
		List<GraphObject> objects = new LinkedList<>();

		final int N = 10;
		for(int i = 0; i < N; i++)
		{
			objects.add(graph_service.AddObject());
		}

		assertEquals(0, graph_service.GetAllLinks().size());

		for(int i = 0; i < N; i++)
		{
			graph_service.AddLink(objects.get(i), objects.get((i * 2) % N), "test");
			assertEquals(i + 1, graph_service.GetAllLinks().size());
		}
	}

	@Test
	public void TestGetAllObjects() throws Exception
	{
		final int N = 10;

		// static must not be visible
		assertEquals(0, graph_service.GetAllObjects().size());

		for(int i = 0; i < N; i++)
		{
			graph_service.AddObject();

			assertEquals(i + 1, graph_service.GetAllObjects().size());
		}
	}

	@Test
	public void TestClean() throws Exception
	{
		GraphObject obj1 = graph_service.AddObject();
		GraphObject obj2 = graph_service.AddObject();
		graph_service.AddObject();
		graph_service.AddLink(obj1, obj2, "test");

		graph_service.Clean();

		assertEquals("unexpected objects found"
			, 0
			, graph_service.GetAllObjects().size());

		assertEquals("some links found"
			, 0
			, graph_service.GetAllLinks().size());
	}
}
