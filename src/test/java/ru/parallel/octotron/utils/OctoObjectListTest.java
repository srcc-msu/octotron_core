package ru.parallel.octotron.utils;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public class OctoObjectListTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		OctoObjectListTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
		OctoObjectListTest.graph_service = new GraphService(OctoObjectListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		OctoObjectListTest.graph.Shutdown();
		OctoObjectListTest.graph.Delete();
	}

	@Test
	public void TestAdd()
	{
		OctoObjectList list = new OctoObjectList();

		assertEquals("list is not empty", list.size(), 0);

		list.add(OctoObjectListTest.graph_service.AddObject());
		assertEquals("list has no elements", list.size(), 1);

		list.add(OctoObjectListTest.graph_service.AddObject());
		assertEquals("list has not get 2nd element", list.size(), 2);

		assertNotNull("add not worked correctly", list.get(0));
	}

	@Test
	public void TestGet()
	{
		OctoObjectList list = new OctoObjectList();

		list.add(OctoObjectListTest.graph_service.AddObject());
		list.add(OctoObjectListTest.graph_service.AddObject());
		list.add(OctoObjectListTest.graph_service.AddObject());

		assertNotNull("got something wrong", list.get(0));
		assertNotNull("got something wrong", list.get(1));
		assertNotNull("got something wrong", list.get(2));
	}

	@Test
	public void TestIterate()
	{
		OctoObjectList list = new OctoObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(OctoObjectListTest.graph_service.AddObject());


		int i = 0;

		for(OctoObject obj : list)
			i++;

		assertEquals("got something wrong", N, i);
	}

	@Test
	public void TestSize()
	{
		OctoObjectList list = new OctoObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(OctoObjectListTest.graph_service.AddObject());
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void TestRange()
	{
		OctoObjectList list = new OctoObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(OctoObjectListTest.graph_service.AddObject());
		}

		assertEquals(N, list.range(0, N).size());
		assertEquals(N/2, list.range(0, N/2).size());
		assertEquals(N/2, list.range(N/2, N).size());

		assertEquals(1, list.range(0, 1).size());
		assertEquals(0, list.range(0, 0).size());
	}

	@Test
	public void Append()
	{
		OctoObjectList list1 = new OctoObjectList();
		OctoObjectList list2 = new OctoObjectList();
		OctoObjectList list3;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list1.add(OctoObjectListTest.graph_service.AddObject());
			list2.add(OctoObjectListTest.graph_service.AddObject());
		}

		list3 = list1.append(list2);

		assertEquals("got something wrong", list1.size(), N);
		assertEquals("got something wrong", list2.size(), N);
		assertEquals("got something wrong", list3.size(), N * 2);
	}
}
