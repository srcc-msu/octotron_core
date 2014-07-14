package ru.parallel.octotron.core.graph.collections;

import org.junit.*;
import static org.junit.Assert.*;

import ru.parallel.octotron.core.graph.impl.GraphLink;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.graph.impl.GraphObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public class ObjectListTest
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		ObjectListTest.graph = new Neo4jGraph( "dbs/" + ObjectListTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		ObjectListTest.graph_service = new GraphService(ObjectListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ObjectListTest.graph.Shutdown();
		ObjectListTest.graph.Delete();
	}

	@Test
	public void TestAdd()
	{
		ObjectList list = new ObjectList();

		assertEquals("list is not empty", list.size(), 0);

		list.add(ObjectListTest.graph_service.AddObject());
		assertEquals("list has no elements", list.size(), 1);

		list.add(ObjectListTest.graph_service.AddObject());
		assertEquals("list has not get 2nd element", list.size(), 2);

		assertNotNull("add not worked correctly", list.get(0));
	}

	@Test
	public void TestGet()
	{
		ObjectList list = new ObjectList();

		list.add(ObjectListTest.graph_service.AddObject());
		list.add(ObjectListTest.graph_service.AddObject());
		list.add(ObjectListTest.graph_service.AddObject());

		assertNotNull("got something wrong", list.get(0));
		assertNotNull("got something wrong", list.get(1));
		assertNotNull("got something wrong", list.get(2));
	}

	@Test
	public void TestIterate()
	{
		ObjectList<GraphObject, GraphLink> list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(ObjectListTest.graph_service.AddObject());


		int i = 0;

		for(GraphObject obj : list)
			i++;

		assertEquals("got something wrong", N, i);
	}

	@Test
	public void TestSize()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(ObjectListTest.graph_service.AddObject());
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void TestRange()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(ObjectListTest.graph_service.AddObject());
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
		ObjectList list1 = new ObjectList();
		ObjectList list2 = new ObjectList();
		ObjectList list3;

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list1.add(ObjectListTest.graph_service.AddObject());
			list2.add(ObjectListTest.graph_service.AddObject());
		}

		list3 = list1.append(list2);

		assertEquals("got something wrong", list1.size(), N);
		assertEquals("got something wrong", list2.size(), N);
		assertEquals("got something wrong", list3.size(), N * 2);
	}
}
