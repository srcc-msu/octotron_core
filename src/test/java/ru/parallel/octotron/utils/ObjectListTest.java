package ru.parallel.octotron.utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

public class ObjectListTest extends Assert
{
	private static GraphService graph_service;
	private static Neo4jGraph graph;

	@BeforeClass
	public static void Init() throws Exception
	{
		ObjectListTest.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
		ObjectListTest.graph_service = new GraphService(ObjectListTest.graph);
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		ObjectListTest.graph.Shutdown();
		ObjectListTest.graph.Delete();
	}

	@Test
	public void Add()
	{
		OctoObjectList list = new OctoObjectList();

		Assert.assertEquals("list is not empty", list.size(), 0);

		list.add(ObjectListTest.graph_service.AddObject());
		Assert.assertEquals("list has no elements", list.size(), 1);

		list.add(ObjectListTest.graph_service.AddObject());
		Assert.assertEquals("list has not get 2nd element", list.size(), 2);

		Assert.assertNotNull("add not worked correctly", list.get(0));
	}

	@Test
	public void Get()
	{
		OctoObjectList list = new OctoObjectList();

		list.add(ObjectListTest.graph_service.AddObject());
		list.add(ObjectListTest.graph_service.AddObject());
		list.add(ObjectListTest.graph_service.AddObject());

		Assert.assertNotNull("got something wrong", list.get(0));
		Assert.assertNotNull("got something wrong", list.get(1));
		Assert.assertNotNull("got something wrong", list.get(2));
	}

	@Test
	public void Iterate()
	{
		OctoObjectList list = new OctoObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(ObjectListTest.graph_service.AddObject());


		int i = 0;

		for(OctoObject obj : list)
			i++;

		Assert.assertEquals("got something wrong", N, i);
	}

	@Test
	public void Size()
	{
		OctoObjectList list = new OctoObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(ObjectListTest.graph_service.AddObject());
			Assert.assertEquals("got something wrong", list.size(), i + 1);
		}
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
			list1.add(ObjectListTest.graph_service.AddObject());
			list2.add(ObjectListTest.graph_service.AddObject());
		}

		list3 = list1.append(list2);

		Assert.assertEquals("got something wrong", list1.size(), N);
		Assert.assertEquals("got something wrong", list2.size(), N);
		Assert.assertEquals("got something wrong", list3.size(), N * 2);
	}
}
