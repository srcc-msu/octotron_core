package test.java;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.ObjectList;

public class TestAutoList extends Assert
{
	static GraphService graph_service;
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		graph.Shutdown();
		try
		{
			graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void Add()
	{
		ObjectList list = new ObjectList();

		assertEquals("list is not empty", list.size(), 0);

		list.add(graph_service.AddObject());
		assertEquals("list has no elements", list.size(), 1);

		list.add(graph_service.AddObject());
		assertEquals("list has not get 2nd element", list.size(), 2);

		try
		{
			assertNotNull("add not worked correctly", list.get(0));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void Get()
	{
		ObjectList list = new ObjectList();

		list.add(graph_service.AddObject());
		list.add(graph_service.AddObject());
		list.add(graph_service.AddObject());

		try
		{
			assertNotNull("got something wrong", list.get(0));
			assertNotNull("got something wrong", list.get(1));
			assertNotNull("got something wrong", list.get(2));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void Iterate()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(graph_service.AddObject());

		try
		{
			int i = 0;

			for(OctoObject obj : list)
				if(obj != null)
					i++;

			assertEquals("got something wrong", N, i);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void Size()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(graph_service.AddObject());
			assertEquals("got something wrong", list.size(), i + 1);
		}
	}

	@Test
	public void Append()
	{
		ObjectList list1 = new ObjectList();
		ObjectList list2 = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list1.add(graph_service.AddObject());
			list2.add(graph_service.AddObject());
		}

		list1.append(list2);

		assertEquals("got something wrong", list1.size(), N * 2);
	}
}
