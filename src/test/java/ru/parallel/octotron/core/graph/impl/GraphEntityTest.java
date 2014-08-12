package ru.parallel.octotron.core.graph.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.*;

public class GraphEntityTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphEntityTest.graph = new Neo4jGraph( "dbs/" + GraphEntityTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
GraphService.Init(graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		GraphEntityTest.graph.Shutdown();
		GraphEntityTest.graph.Delete();
	}

	@After
	public void Clean()
	{
		GraphService.Get().Clean();
	}

	@Test
	public void TestSetAttribute() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		GraphLink link = GraphService.Get().AddLink(object1, object2, "test");

		assertFalse(object1.TestAttribute("exist"));
		assertFalse(object2.TestAttribute("exist"));
		assertFalse(link.TestAttribute("exist"));

		object1.DeclareAttribute("exist", "");

		assertTrue(object1.TestAttribute("exist"));
		assertFalse(object2.TestAttribute("exist"));
		assertFalse(link.TestAttribute("exist"));

		link.DeclareAttribute("exist", "");

		assertTrue(object1.TestAttribute("exist"));
		assertFalse(object2.TestAttribute("exist"));
		assertTrue(link.TestAttribute("exist"));

		object2.DeclareAttribute("exist", "");

		assertTrue(object1.TestAttribute("exist"));
		assertTrue(object2.TestAttribute("exist"));
		assertTrue(link.TestAttribute("exist"));
	}

	@Test
	public void TestGetAttribute() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		object1.DeclareAttribute("test_long", 1L);
		object1.DeclareAttribute("test_str", "a");

		object1.DeclareAttribute("test_double", 1.0);
		object1.DeclareAttribute("test_bool", true);

		assertEquals(Long.valueOf(1L)
			, object1.GetAttribute("test_long").GetLong());

		assertEquals("a", object1.GetAttribute("test_str").GetString());

		assertEquals(1.0, object1.GetAttribute("test_double").GetDouble(), 0.1);

		assertEquals(true, object1.GetAttribute("test_bool").GetBoolean());

		GraphLink link = GraphService.Get().AddLink(object1, object2, "test");

		link.DeclareAttribute("test_long", 1L);
		link.DeclareAttribute("test_str", "a");

		link.DeclareAttribute("test_double", 1.0);
		link.DeclareAttribute("test_bool", true);

		assertEquals(Long.valueOf(1L)
			,link.GetAttribute("test_long").GetLong());

		assertEquals("a", link.GetAttribute("test_str").GetString());

		assertEquals(1.0, link.GetAttribute("test_double").GetDouble(), 0.1);

		assertEquals(true, link.GetAttribute("test_bool").GetBoolean());
	}

	@Test
	public void TestTestAttribute() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		object1.DeclareAttribute("object", "");

		assertFalse(object1.TestAttribute("not_exist"));
		assertFalse(object1.TestAttribute("link"));
		assertTrue(object1.TestAttribute("object"));

		GraphLink link = GraphService.Get().AddLink(object1, object2, "test");
		link.DeclareAttribute("link", "");

		assertFalse(link.TestAttribute("not_exist"));
		assertFalse(link.TestAttribute("object"));
		assertTrue(link.TestAttribute("link"));
	}

	@Test
	public void TestDeleteAttribute() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		GraphLink link = GraphService.Get().AddLink(object1, object2, "test");

		object1.DeclareAttribute("object", "");
		link.DeclareAttribute("link", "");

		assertTrue(object1.TestAttribute("object"));
		assertTrue(link.TestAttribute("link"));

		object1.DeleteAttribute("object");

		assertFalse(object1.TestAttribute("object"));
		assertTrue(link.TestAttribute("link"));

		link.DeleteAttribute("link");

		assertFalse(object1.TestAttribute("object"));
		assertFalse(link.TestAttribute("link"));
	}

	@Test
	public void TestAddSelfLink() throws Exception
	{
		GraphObject object = GraphService.Get().AddObject();

		assertEquals(0, object.GetOutLinks().size());
		assertEquals(0, object.GetInLinks().size());

		GraphService.Get().AddLink(object, object, "test");

		assertEquals(1, object.GetOutLinks().size());
		assertEquals(1, object.GetInLinks().size());
	}

	@Test
	public void TestAddLink() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		assertEquals(0, GraphService.Get().GetAllLinks().size());

		GraphService.Get().AddLink(object1, object2, "test");

		assertEquals(0, object1.GetInLinks().size());
		assertEquals(1, object1.GetOutLinks().size());

		assertEquals(1, object2.GetInLinks().size());
		assertEquals(0, object2.GetOutLinks().size());

		assertEquals(1, GraphService.Get().GetAllLinks().size());

		GraphService.Get().AddLink(object2, object1, "test");

		assertEquals(1, object1.GetInLinks().size());
		assertEquals(1, object1.GetOutLinks().size());

		assertEquals(1, object2.GetInLinks().size());
		assertEquals(1, object2.GetOutLinks().size());

		assertEquals(2, GraphService.Get().GetAllLinks().size());
	}

	@Test
	public void TestAddObject() throws Exception
	{
		assertEquals(0, GraphService.Get().GetAllObjects().size());
		GraphService.Get().AddObject();
		assertEquals(1, GraphService.Get().GetAllObjects().size());
		GraphService.Get().AddObject();
		assertEquals(2, GraphService.Get().GetAllObjects().size());
	}

	@Test
	public void TestDelete() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();

		assertEquals(0, GraphService.Get().GetAllLinks().size());
		GraphLink link1 = GraphService.Get().AddLink(object1, object2, "test");
		assertEquals(1, GraphService.Get().GetAllLinks().size());
		GraphLink link2 = GraphService.Get().AddLink(object1, object2, "test");
		assertEquals(2, GraphService.Get().GetAllLinks().size());
		link1.Delete();
		assertEquals(1, GraphService.Get().GetAllLinks().size());
		link2.Delete();
		assertEquals(0, GraphService.Get().GetAllLinks().size());

		assertEquals(2, GraphService.Get().GetAllObjects().size());
		object2.Delete();
		assertEquals(1, GraphService.Get().GetAllObjects().size());
		object1.Delete();
		assertEquals(0, GraphService.Get().GetAllObjects().size());
	}

	@Test
	public void TestGetAttributes() throws Exception
	{
		GraphObject object1 = GraphService.Get().AddObject();
		GraphObject object2 = GraphService.Get().AddObject();
		GraphLink link = GraphService.Get().AddLink(object1, object2, "test");

// everyone has AID
		assertEquals(1, object1.GetAttributes().size());
		assertEquals(1, object2.GetAttributes().size());
		assertEquals(1, link.GetAttributes().size());

		final int N = 10;

		for(long i = 0; i < N; i++)
		{
			object1.DeclareAttribute("test" + i, i);
			link.DeclareAttribute("test" + i, N + i);
		}

		for(long i = 0; i < N; i++)
		{
			assertEquals(Long.valueOf(i)
				, object1.GetAttribute("test" + i).GetLong());
			assertEquals(Long.valueOf(N + i)
				, link.GetAttribute("test" + i).GetLong());
		}

		assertEquals(1 + N, object1.GetAttributes().size());
		assertEquals(1 + 0, object2.GetAttributes().size());
		assertEquals(1 + N, link.GetAttributes().size());
	}

}