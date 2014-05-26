package ru.parallel.octotron.core;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.utils.OctoObjectList;

import java.util.LinkedList;
import java.util.List;

public class GraphServiceTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		GraphServiceTest.graph = new Neo4jGraph( "dbs/" + GraphServiceTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphServiceTest.graph_service = new GraphService(GraphServiceTest.graph);
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
	public void TestIsStaticName() throws Exception
	{
		assertFalse(GraphService.IsStaticName("test"));
		assertTrue(GraphService.IsStaticName("_static_test"));
	}

	@Test
	public void TestSetAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		assertFalse(graph_service.TestAttribute(object1, "exist"));
		assertFalse(graph_service.TestAttribute(object2, "exist"));
		assertFalse(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(object1, "exist", "");

		assertTrue(graph_service.TestAttribute(object1, "exist"));
		assertFalse(graph_service.TestAttribute(object2, "exist"));
		assertFalse(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(link, "exist", "");

		assertTrue(graph_service.TestAttribute(object1, "exist"));
		assertFalse(graph_service.TestAttribute(object2, "exist"));
		assertTrue(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(object2, "exist", "");

		assertTrue(graph_service.TestAttribute(object1, "exist"));
		assertTrue(graph_service.TestAttribute(object2, "exist"));
		assertTrue(graph_service.TestAttribute(link, "exist"));

		boolean catched = false;

		try
		{
			graph_service.SetAttribute(graph_service.GetStatic(), "test", "");
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true; // no usual names for static
		}
		assertTrue(catched);
	}

	@Test
	public void TestGetAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		graph_service.SetAttribute(object1, "test_long", 1L);
		graph_service.SetAttribute(object1, "test_str", "a");

		graph_service.SetAttribute(object1, "test_double", 1.0);
		graph_service.SetAttribute(object1, "test_bool", true);

		assertEquals(Long.valueOf(1L)
			, graph_service.GetAttribute(object1, "test_long").GetLong());

		assertEquals("a", graph_service.GetAttribute(object1, "test_str").GetString());

		assertEquals(1.0, graph_service.GetAttribute(object1, "test_double").GetDouble(), 0.1);

		assertEquals(true, graph_service.GetAttribute(object1, "test_bool").GetBoolean());

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(link, "test_long", 1L);
		graph_service.SetAttribute(link, "test_str", "a");

		graph_service.SetAttribute(link, "test_double", 1.0);
		graph_service.SetAttribute(link, "test_bool", true);

		assertEquals(Long.valueOf(1L)
			,graph_service.GetAttribute(link, "test_long").GetLong());

		assertEquals("a", graph_service.GetAttribute(link, "test_str").GetString());

		assertEquals(1.0, graph_service.GetAttribute(link, "test_double").GetDouble(), 0.1);

		assertEquals(true, graph_service.GetAttribute(link, "test_bool").GetBoolean());
	}

	@Test
	public void TestTestAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		graph_service.SetAttribute(object1, "object", "");

		assertFalse(graph_service.TestAttribute(object1, "not_exist"));
		assertFalse(graph_service.TestAttribute(object1, "link"));
		assertTrue(graph_service.TestAttribute(object1, "object"));

		OctoLink link = graph_service.AddLink(object1, object2, "test");
		graph_service.SetAttribute(link, "link", "");

		assertFalse(graph_service.TestAttribute(link, "not_exist"));
		assertFalse(graph_service.TestAttribute(link, "object"));
		assertTrue(graph_service.TestAttribute(link, "link"));
	}

	@Test
	public void TestDeleteAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "object", "");
		graph_service.SetAttribute(link, "link", "");

		assertTrue(graph_service.TestAttribute(object1, "object"));
		assertTrue(graph_service.TestAttribute(link, "link"));

		graph_service.DeleteAttribute(object1, "object");

		assertFalse(graph_service.TestAttribute(object1, "object"));
		assertTrue (graph_service.TestAttribute(link, "link"));

		graph_service.DeleteAttribute(link, "link");

		assertFalse(graph_service.TestAttribute(object1, "object"));
		assertFalse(graph_service.TestAttribute(link, "link"));

		boolean catched = false;

		try
		{
			graph_service.Delete(graph_service.GetStatic());
		}
		catch(ExceptionModelFail ignore)
		{
			catched = true; // no deleting static
		}
		assertTrue(catched);
	}

	@Test
	public void TestGetStatic() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "_static_object", "");
		graph_service.SetAttribute(link, "_static_link", "");

		assertEquals("", graph_service.GetAttribute(object1, "_static_object").GetString());
		assertEquals("", graph_service.GetAttribute(object1, "_static_link").GetString());

		assertEquals("", graph_service.GetAttribute(object2, "_static_object").GetString());
		assertEquals("", graph_service.GetAttribute(object2, "_static_link").GetString());

		assertEquals("", graph_service.GetAttribute(link, "_static_object").GetString());
		assertEquals("", graph_service.GetAttribute(link, "_static_link").GetString());
	}

	@Test
	public void TestAddSelfLink() throws Exception
	{
		OctoObject object = graph_service.AddObject();

		assertEquals(0, object.GetOutLinks().size());
		assertEquals(0, object.GetInLinks().size());

		graph_service.AddLink(object, object, "test");

		assertEquals(1, object.GetOutLinks().size());
		assertEquals(1, object.GetInLinks().size());
	}

	@Test
	public void TestAddLink() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		assertEquals(0, graph_service.GetAllLinks().size());

		graph_service.AddLink(object1, object2, "test");

		assertEquals(0, graph_service.GetInLinks(object1).size());
		assertEquals(1, graph_service.GetOutLinks(object1).size());

		assertEquals(1, graph_service.GetInLinks(object2).size());
		assertEquals(0, graph_service.GetOutLinks(object2).size());

		assertEquals(1, graph_service.GetAllLinks().size());

		graph_service.AddLink(object2, object1, "test");

		assertEquals(1, graph_service.GetInLinks(object1).size());
		assertEquals(1, graph_service.GetOutLinks(object1).size());

		assertEquals(1, graph_service.GetInLinks(object2).size());
		assertEquals(1, graph_service.GetOutLinks(object2).size());

		assertEquals(2, graph_service.GetAllLinks().size());
	}

	@Test
	public void TestAddObject() throws Exception
	{
		assertEquals(0, graph_service.GetAllObjects().size());
		graph_service.AddObject();
		assertEquals(1, graph_service.GetAllObjects().size());
		graph_service.AddObject();
		assertEquals(2, graph_service.GetAllObjects().size());
	}

	@Test
	public void TestDelete() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		assertEquals(0, graph_service.GetAllLinks().size());
		OctoLink link1 = graph_service.AddLink(object1, object2, "test");
		assertEquals(1, graph_service.GetAllLinks().size());
		OctoLink link2 = graph_service.AddLink(object1, object2, "test");
		assertEquals(2, graph_service.GetAllLinks().size());
		graph_service.Delete(link1);
		assertEquals(1, graph_service.GetAllLinks().size());
		graph_service.Delete(link2);
		assertEquals(0, graph_service.GetAllLinks().size());

		assertEquals(2, graph_service.GetAllObjects().size());
		graph_service.Delete(object2);
		assertEquals(1, graph_service.GetAllObjects().size());
		graph_service.Delete(object1);
		assertEquals(0, graph_service.GetAllObjects().size());
	}

	@Test
	public void TestGetInLinks() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		assertEquals(0, graph_service.GetInLinks(object1).size());
		assertEquals(0, graph_service.GetInLinks(object2).size());

		OctoLink link1 = graph_service.AddLink(object1, object2, "test");

		assertEquals(link1.GetUID().getUid()
			, graph_service.GetInLinks(object2).Only().GetUID().getUid());

		OctoLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetUID().getUid()
			, graph_service.GetInLinks(object1).Only().GetUID().getUid());
	}

	@Test
	public void TestGetOutLink() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		assertEquals(0, graph_service.GetInLinks(object1).size());
		assertEquals(0, graph_service.GetInLinks(object2).size());

		OctoLink link1 = graph_service.AddLink(object1, object2, "test");

		assertEquals(link1.GetUID().getUid()
			, graph_service.GetOutLinks(object1).Only().GetUID().getUid());

		OctoLink link2 = graph_service.AddLink(object2, object1, "test");

		assertEquals(link2.GetUID().getUid()
			, graph_service.GetOutLinks(object2).Only().GetUID().getUid());
	}

	@Test
	public void TestGetAttributes() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

// everyone has AID
		assertEquals(1, object1.GetAttributes().size());
		assertEquals(1, object2.GetAttributes().size());
		assertEquals(1, link.GetAttributes().size());

		final int N = 10;

		for(long i = 0; i < N; i++)
		{
			graph_service.SetAttribute(object1, "test" + i, i);
			graph_service.SetAttribute(link, "test" + i, N + i);
		}

		for(long i = 0; i < N; i++)
		{
			assertEquals(Long.valueOf(i)
				, graph_service.GetAttribute(object1, "test" + i).GetLong());
			assertEquals(Long.valueOf(N + i)
				, graph_service.GetAttribute(link, "test" + i).GetLong());
		}

		assertEquals(1 + N, object1.GetAttributes().size());
		assertEquals(1 + 0, object2.GetAttributes().size());
		assertEquals(1 + N, link.GetAttributes().size());
	}

	@Test
	public void TestSetMeta() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

// everyone has AID
		assertEquals(1, object1.GetAttributes().size());
		assertEquals(1, object2.GetAttributes().size());
		assertEquals(1, link.GetAttributes().size());

		assertEquals(0, graph_service.GetAllMeta(object1).size());
		assertEquals(0, graph_service.GetAllMeta(object2).size());
		assertEquals(0, graph_service.GetAllMeta(link).size());

		graph_service.SetAttribute(object1, "object", "");
		graph_service.SetAttribute(link, "link", "");

// everyone has AID
		assertEquals(2, object1.GetAttributes().size());
		assertEquals(1, object2.GetAttributes().size());
		assertEquals(2, link.GetAttributes().size());

		graph_service.SetMeta(object1, "object", "object_meta", 0);
		graph_service.SetMeta(link, "link", "link_meta", 0);

		assertEquals(1, graph_service.GetAllMeta(object1).size());
		assertEquals(0, graph_service.GetAllMeta(object2).size());
		assertEquals(1, graph_service.GetAllMeta(link).size());

// everyone has AID
		assertEquals(2, object1.GetAttributes().size());
		assertEquals(1, object2.GetAttributes().size());
		assertEquals(2, link.GetAttributes().size());
	}

	@Test
	public void TestGetMeta() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "object", "");

		graph_service.SetAttribute(link, "link", "");

		graph_service.SetMeta(object1, "object", "object_meta_1", 1);
		graph_service.SetMeta(object1, "object", "object_meta_2", 2);

		graph_service.SetMeta(link, "link", "link_meta_1", 1);
		graph_service.SetMeta(link, "link", "link_meta_2", 2);

		assertEquals(1, graph_service.GetMeta(object1, "object", "object_meta_1"));
		assertEquals(2, graph_service.GetMeta(object1, "object", "object_meta_2"));

		assertEquals(1, graph_service.GetMeta(link, "link", "link_meta_1"));
		assertEquals(2, graph_service.GetMeta(link, "link", "link_meta_2"));
	}

	@Test
	public void TestTestMeta() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "object", "");
		graph_service.SetAttribute(object1, "object2", "");

		graph_service.SetAttribute(link, "link", "");
		graph_service.SetAttribute(link, "link2", "");

		graph_service.SetMeta(object1, "object", "object_meta", 1);
		graph_service.SetMeta(link, "link", "link_meta", 1);

		assertTrue (graph_service.TestMeta(object1, "object", "object_meta"));
		assertFalse(graph_service.TestMeta(object1, "object2", "object_meta"));

		assertTrue (graph_service.TestMeta(link, "link", "link_meta"));
		assertFalse(graph_service.TestMeta(link, "link2", "link_meta"));
	}

	@Test
	public void TestDeleteMeta() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "object", "");

		graph_service.SetAttribute(link, "link", "");

		graph_service.SetMeta(object1, "object", "object_meta", 1);
		graph_service.SetMeta(link, "link", "link_meta", 1);

		graph_service.DeleteMeta(object1, "object", "object_meta");
		graph_service.DeleteMeta(link, "link", "link_meta");

		assertFalse(graph_service.TestMeta(object1, "object", "object_meta"));
		assertFalse(graph_service.TestMeta(link, "link", "link_meta"));
	}

	@Test
	public void TestGetAllLinks() throws Exception
	{
		OctoObjectList objects = new OctoObjectList();

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
	public void TestSetArray() throws Exception
	{
		final int N = 10;

		List<Long> data = new LinkedList<>();

		OctoObject object = graph_service.AddObject();

		for(long i = 0; i < N; i++)
			data.add(i);

		assertEquals(1, graph_service.GetAttributes(object).size());

		graph_service.SetArray(object, "test", data);

		// array must not be visible
		assertEquals(1, graph_service.GetAttributes(object).size());
	}

	@Test
	public void TestGetArray() throws Exception
	{
		final int N = 10;

		List<Long> data1 = new LinkedList<>();
		List<Long> data2 = new LinkedList<>();

		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		for(long i = 0; i < N; i++)
		{
			data1.add(i);
			data2.add(i * 2);
		}

		graph_service.SetArray(object1, "test", data1);
		graph_service.SetArray(object2, "test", data2);

		List<Long> test1 = graph_service.GetArray(object1, "test");
		List<Long> test2 = graph_service.GetArray(object2, "test");

		for(int i = 0; i < N; i++)
		{
			assertEquals(Long.valueOf(i), test1.get(i));
			assertEquals(Long.valueOf(i * 2), test2.get(i));
		}
	}

	@Test
	public void TestAddToArray() throws Exception
	{
		final int N = 10;

		List<Long> data = new LinkedList<>();
		data.add(0L);

		OctoObject object = graph_service.AddObject();

		graph_service.SetArray(object, "test", data);

		for(long iter = 1; iter < N; iter++)
		{
			graph_service.AddToArray(object, "test", iter);
			List<Long> test = graph_service.GetArray(object, "test");

			for(int i = 0; i <= iter; i++)
				assertEquals(Long.valueOf(i), test.get(i));
		}
	}

	@Test
	public void TestCleanArray() throws Exception
	{
		final int N = 10;

		List<Long> data1 = new LinkedList<>();
		List<Long> data2 = new LinkedList<>();

		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		for(long i = 0; i < N; i++)
		{
			data1.add(i);
			data2.add(i * 2);
		}

		graph_service.SetArray(object1, "test", data1);
		graph_service.SetArray(object2, "test", data2);

		assertEquals(N, graph_service.GetArray(object1, "test").size());
		assertEquals(N, graph_service.GetArray(object2, "test").size());

		graph_service.CleanArray(object1, "test");

		assertEquals(0, graph_service.GetArray(object1, "test").size());
		assertEquals(N, graph_service.GetArray(object2, "test").size());

		graph_service.CleanArray(object2, "test");

		assertEquals(0, graph_service.GetArray(object1, "test").size());
		assertEquals(0, graph_service.GetArray(object2, "test").size());
	}

	@Test
	public void TestClean() throws Exception
	{
		OctoObject obj1 = graph_service.AddObject();
		OctoObject obj2 = graph_service.AddObject();
		graph_service.AddObject();
		graph_service.AddLink(obj1, obj2, "test");

		graph_service.Clean();

		assertEquals("unexpected objects found"
			, 0
			, graph_service.GetAllObjects().size());

		assertEquals("some links found"
			, 0
			, graph_service.GetAllLinks().size());

		assertEquals("static has unexpected attributes"
			, 3 // type, AID, next_AID
			, graph_service.GetStatic().GetAttributes().size());
	}
}