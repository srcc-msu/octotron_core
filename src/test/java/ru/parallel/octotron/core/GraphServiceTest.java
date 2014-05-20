package ru.parallel.octotron.core;

import org.junit.*;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;

public class GraphServiceTest
{
	static GraphService graph_service;
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			GraphServiceTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			GraphServiceTest.graph_service = new GraphService(GraphServiceTest.graph);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		GraphServiceTest.graph.Shutdown();
		try
		{
			GraphServiceTest.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@After
	public void Clean()
	{
		graph_service.Clean();
	}

	@Test
	public void TestIsStaticName() throws Exception
	{
		Assert.assertFalse(graph_service.IsStaticName("test"));
		Assert.assertTrue(graph_service.IsStaticName("_static_test"));
	}

	@Test
	public void TestSetAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		Assert.assertFalse(graph_service.TestAttribute(object1, "exist"));
		Assert.assertFalse(graph_service.TestAttribute(object2, "exist"));
		Assert.assertFalse(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(object1, "exist", "");

		Assert.assertTrue(graph_service.TestAttribute(object1, "exist"));
		Assert.assertFalse(graph_service.TestAttribute(object2, "exist"));
		Assert.assertFalse(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(link, "exist", "");

		Assert.assertTrue(graph_service.TestAttribute(object1, "exist"));
		Assert.assertFalse(graph_service.TestAttribute(object2, "exist"));
		Assert.assertTrue(graph_service.TestAttribute(link, "exist"));

		graph_service.SetAttribute(object2, "exist", "");

		Assert.assertTrue(graph_service.TestAttribute(object1, "exist"));
		Assert.assertTrue(graph_service.TestAttribute(object2, "exist"));
		Assert.assertTrue(graph_service.TestAttribute(link, "exist"));
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

		Assert.assertEquals(Long.valueOf(1L)
			, graph_service.GetAttribute(object1, "test_long").GetLong());

		Assert.assertEquals("a", graph_service.GetAttribute(object1, "test_str").GetString());

		Assert.assertEquals(1.0, graph_service.GetAttribute(object1, "test_double").GetDouble(), 0.1);

		Assert.assertEquals(true, graph_service.GetAttribute(object1, "test_bool").GetBoolean());

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(link, "test_long", 1L);
		graph_service.SetAttribute(link, "test_str", "a");

		graph_service.SetAttribute(link, "test_double", 1.0);
		graph_service.SetAttribute(link, "test_bool", true);

		Assert.assertEquals(Long.valueOf(1L)
			,graph_service.GetAttribute(link, "test_long").GetLong());

		Assert.assertEquals("a", graph_service.GetAttribute(link, "test_str").GetString());

		Assert.assertEquals(1.0, graph_service.GetAttribute(link, "test_double").GetDouble(), 0.1);

		Assert.assertEquals(true, graph_service.GetAttribute(link, "test_bool").GetBoolean());
	}

	@Test
	public void TestTestAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		graph_service.SetAttribute(object1, "object", "");

		Assert.assertFalse(graph_service.TestAttribute(object1, "not_exist"));
		Assert.assertFalse(graph_service.TestAttribute(object1, "link"));
		Assert.assertTrue(graph_service.TestAttribute(object1, "object"));

		OctoLink link = graph_service.AddLink(object1, object2, "test");
		graph_service.SetAttribute(link, "link", "");

		Assert.assertFalse(graph_service.TestAttribute(link, "not_exist"));
		Assert.assertFalse(graph_service.TestAttribute(link, "object"));
		Assert.assertTrue(graph_service.TestAttribute(link, "link"));
	}

	@Test
	public void TestDeleteAttribute() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "object", "");
		graph_service.SetAttribute(link, "link", "");

		Assert.assertTrue(graph_service.TestAttribute(object1, "object"));
		Assert.assertTrue(graph_service.TestAttribute(link, "link"));

		graph_service.DeleteAttribute(object1, "object");

		Assert.assertFalse(graph_service.TestAttribute(object1, "object"));
		Assert.assertTrue (graph_service.TestAttribute(link, "link"));

		graph_service.DeleteAttribute(link, "link");

		Assert.assertFalse(graph_service.TestAttribute(object1, "object"));
		Assert.assertFalse(graph_service.TestAttribute(link, "link"));
	}

	@Test
	public void TestGetStatic() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

		graph_service.SetAttribute(object1, "_static_object", "");
		graph_service.SetAttribute(link, "_static_link", "");

		Assert.assertEquals("", graph_service.GetAttribute(object1, "_static_object").GetString());
		Assert.assertEquals("", graph_service.GetAttribute(object1, "_static_link").GetString());

		Assert.assertEquals("", graph_service.GetAttribute(object2, "_static_object").GetString());
		Assert.assertEquals("", graph_service.GetAttribute(object2, "_static_link").GetString());

		Assert.assertEquals("", graph_service.GetAttribute(link, "_static_object").GetString());
		Assert.assertEquals("", graph_service.GetAttribute(link, "_static_link").GetString());
	}

	@Test
	public void TestAddLink() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		Assert.assertEquals(0, graph_service.GetAllLinks().size());

		graph_service.AddLink(object1, object2, "test");

		Assert.assertEquals(0, graph_service.GetInLinks(object1).size());
		Assert.assertEquals(1, graph_service.GetOutLinks(object1).size());

		Assert.assertEquals(1, graph_service.GetInLinks(object2).size());
		Assert.assertEquals(0, graph_service.GetOutLinks(object2).size());

		Assert.assertEquals(1, graph_service.GetAllLinks().size());

		graph_service.AddLink(object2, object1, "test");

		Assert.assertEquals(1, graph_service.GetInLinks(object1).size());
		Assert.assertEquals(1, graph_service.GetOutLinks(object1).size());

		Assert.assertEquals(1, graph_service.GetInLinks(object2).size());
		Assert.assertEquals(1, graph_service.GetOutLinks(object2).size());

		Assert.assertEquals(2, graph_service.GetAllLinks().size());
	}

	@Test
	public void TestAddObject() throws Exception
	{

		Assert.assertEquals(0, graph_service.GetAllObjects().size());
		graph_service.AddObject();
		Assert.assertEquals(1, graph_service.GetAllObjects().size());
		graph_service.AddObject();
		Assert.assertEquals(2, graph_service.GetAllObjects().size());
	}

	@Test
	public void TestDelete() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		Assert.assertEquals(2, graph_service.GetAllObjects().size());
		graph_service.Delete(object2);
		Assert.assertEquals(1, graph_service.GetAllObjects().size());
		graph_service.Delete(object1);
		Assert.assertEquals(0, graph_service.GetAllObjects().size());
	}

	@Test
	public void TestGetInLinks() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		Assert.assertEquals(0, graph_service.GetInLinks(object1).size());
		Assert.assertEquals(0, graph_service.GetInLinks(object2).size());

		OctoLink link1 = graph_service.AddLink(object1, object2, "test");

		Assert.assertEquals(link1.GetUID().getUid()
			, graph_service.GetInLinks(object2).Only().GetUID().getUid());

		OctoLink link2 = graph_service.AddLink(object2, object1, "test");

		Assert.assertEquals(link2.GetUID().getUid()
			, graph_service.GetInLinks(object1).Only().GetUID().getUid());
	}

	@Test
	public void TestGetOutLink() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();

		Assert.assertEquals(0, graph_service.GetInLinks(object1).size());
		Assert.assertEquals(0, graph_service.GetInLinks(object2).size());

		OctoLink link1 = graph_service.AddLink(object1, object2, "test");

		Assert.assertEquals(link1.GetUID().getUid()
			, graph_service.GetOutLinks(object1).Only().GetUID().getUid());

		OctoLink link2 = graph_service.AddLink(object2, object1, "test");

		Assert.assertEquals(link2.GetUID().getUid()
			, graph_service.GetOutLinks(object2).Only().GetUID().getUid());
	}

	@Test
	public void TestGetAttributes() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

// everyone has AID
		Assert.assertEquals(1, object1.GetAttributes().size());
		Assert.assertEquals(1, object2.GetAttributes().size());
		Assert.assertEquals(1, link.GetAttributes().size());

		final int N = 10;

		for(long i = 0; i < N; i++)
		{
			graph_service.SetAttribute(object1, "test" + i, i);
			graph_service.SetAttribute(link, "test" + i, N + i);
		}

		for(long i = 0; i < N; i++)
		{
			Assert.assertEquals(Long.valueOf(i)
				, graph_service.GetAttribute(object1, "test" + i).GetLong());
			Assert.assertEquals(Long.valueOf(N + i)
				, graph_service.GetAttribute(link, "test" + i).GetLong());
		}

		Assert.assertEquals(1 + N, object1.GetAttributes().size());
		Assert.assertEquals(1 + 0, object2.GetAttributes().size());
		Assert.assertEquals(1 + N, link.GetAttributes().size());
	}

	@Test
	public void TestSetMeta() throws Exception
	{
		OctoObject object1 = graph_service.AddObject();
		OctoObject object2 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(object1, object2, "test");

// everyone has AID
		Assert.assertEquals(1, object1.GetAttributes().size());
		Assert.assertEquals(1, object2.GetAttributes().size());
		Assert.assertEquals(1, link.GetAttributes().size());

		Assert.assertEquals(0, graph_service.GetAllMeta(object1).size());
		Assert.assertEquals(0, graph_service.GetAllMeta(object2).size());
		Assert.assertEquals(0, graph_service.GetAllMeta(link).size());

		graph_service.SetAttribute(object1, "object", "");
		graph_service.SetAttribute(link, "link", "");

// everyone has AID
		Assert.assertEquals(2, object1.GetAttributes().size());
		Assert.assertEquals(1, object2.GetAttributes().size());
		Assert.assertEquals(2, link.GetAttributes().size());

		graph_service.SetMeta(object1, "object", "object_meta", 0);
		graph_service.SetMeta(link, "link", "link_meta", 0);

		Assert.assertEquals(1, graph_service.GetAllMeta(object1).size());
		Assert.assertEquals(0, graph_service.GetAllMeta(object2).size());
		Assert.assertEquals(1, graph_service.GetAllMeta(link).size());

// everyone has AID
		Assert.assertEquals(2, object1.GetAttributes().size());
		Assert.assertEquals(1, object2.GetAttributes().size());
		Assert.assertEquals(2, link.GetAttributes().size());
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

		Assert.assertEquals(1, graph_service.GetMeta(object1, "object", "object_meta_1"));
		Assert.assertEquals(2, graph_service.GetMeta(object1, "object", "object_meta_2"));

		Assert.assertEquals(1, graph_service.GetMeta(link, "link", "link_meta_1"));
		Assert.assertEquals(2, graph_service.GetMeta(link, "link", "link_meta_2"));
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

		Assert.assertTrue (graph_service.TestMeta(object1, "object", "object_meta"));
		Assert.assertFalse(graph_service.TestMeta(object1, "object2", "object_meta"));

		Assert.assertTrue (graph_service.TestMeta(link, "link", "link_meta"));
		Assert.assertFalse(graph_service.TestMeta(link, "link2", "link_meta"));
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

		Assert.assertFalse(graph_service.TestMeta(object1, "object", "object_meta"));
		Assert.assertFalse(graph_service.TestMeta(link, "link", "link_meta"));
	}

	@Test
	public void TestGetAllLinks() throws Exception
	{

	}

	@Test
	public void TestGetAllObjects() throws Exception
	{

	}

	@Test
	public void TestSetArray() throws Exception
	{

	}

	@Test
	public void TestGetArray() throws Exception
	{

	}

	@Test
	public void TestAddToArray() throws Exception
	{

	}

	@Test
	public void TestCleanArray() throws Exception
	{

	}

	@Test
	public void TestClean() throws Exception
	{
		OctoObject obj1 = graph_service.AddObject();
		OctoObject obj2 = graph_service.AddObject();
		OctoObject obj3 = graph_service.AddObject();
		OctoLink link = graph_service.AddLink(obj1, obj2, "test");

		graph_service.Clean();

		Assert.assertEquals("unexpected objects found"
			, 0
			, graph_service.GetAllObjects().size());

		Assert.assertEquals("some links found"
			, 0
			, graph_service.GetAllLinks().size());

		Assert.assertEquals("static has unexpected attributes"
			, 3 // type, AID, next_AID
			, graph_service.GetStatic().GetAttributes().size());
	}
}