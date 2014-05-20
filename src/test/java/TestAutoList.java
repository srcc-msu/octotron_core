package test.java;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.parallel.octotron.core.GraphService;
import ru.parallel.octotron.core.OctoObject;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import ru.parallel.octotron.utils.ObjectList;

public class TestAutoList extends Assert
{
	static GraphService graph_service;
	static Neo4jGraph graph;

	@BeforeClass
	public static void Init()
	{
		try
		{
			TestAutoList.graph = new Neo4jGraph("dbs/test_neo4j", Neo4jGraph.Op.RECREATE);
			TestAutoList.graph_service = new GraphService(TestAutoList.graph);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@AfterClass
	public static void Delete()
	{
		TestAutoList.graph.Shutdown();
		try
		{
			TestAutoList.graph.Delete();
		}
		catch (ExceptionSystemError e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void Add()
	{
		ObjectList list = new ObjectList();

		Assert.assertEquals("list is not empty", list.size(), 0);

		list.add(TestAutoList.graph_service.AddObject());
		Assert.assertEquals("list has no elements", list.size(), 1);

		list.add(TestAutoList.graph_service.AddObject());
		Assert.assertEquals("list has not get 2nd element", list.size(), 2);

		try
		{
			Assert.assertNotNull("add not worked correctly", list.get(0));
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void Get()
	{
		ObjectList list = new ObjectList();

		list.add(TestAutoList.graph_service.AddObject());
		list.add(TestAutoList.graph_service.AddObject());
		list.add(TestAutoList.graph_service.AddObject());

		try
		{
			Assert.assertNotNull("got something wrong", list.get(0));
			Assert.assertNotNull("got something wrong", list.get(1));
			Assert.assertNotNull("got something wrong", list.get(2));
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void Iterate()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
			list.add(TestAutoList.graph_service.AddObject());

		try
		{
			int i = 0;

			for(OctoObject obj : list)
				i++;

			Assert.assertEquals("got something wrong", N, i);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void Size()
	{
		ObjectList list = new ObjectList();

		int N = 10;

		for(int i = 0; i < N; i++)
		{
			list.add(TestAutoList.graph_service.AddObject());
			Assert.assertEquals("got something wrong", list.size(), i + 1);
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
			list1.add(TestAutoList.graph_service.AddObject());
			list2.add(TestAutoList.graph_service.AddObject());
		}

		list1.append(list2);

		Assert.assertEquals("got something wrong", list1.size(), N * 2);
	}
}
