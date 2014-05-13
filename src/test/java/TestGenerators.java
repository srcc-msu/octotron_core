package test.java;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.ru.parallel.octotron.core.GraphService;
import main.java.ru.parallel.octotron.core.OctoLink;
import main.java.ru.parallel.octotron.core.OctoObject;
import main.java.ru.parallel.octotron.impl.generators.LinkFactory;
import main.java.ru.parallel.octotron.impl.generators.ObjectFactory;
import main.java.ru.parallel.octotron.neo4j.impl.Neo4jGraph;
import main.java.ru.parallel.octotron.primitive.SimpleAttribute;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionModelFail;
import main.java.ru.parallel.octotron.primitive.exception.ExceptionSystemError;
import main.java.ru.parallel.octotron.utils.ObjectList;

/**
 * test some common cases -attributes, factories
 * */
public class TestGenerators extends Assert
{
	static Neo4jGraph graph;
	static ObjectFactory obj_factory;
	static LinkFactory link_factory;

	static int N = 10; // some testing param
	private static GraphService graph_service;

	@BeforeClass
	public static void Init()
	{
		try
		{
			graph = new Neo4jGraph("dbs/test_primitives", Neo4jGraph.Op.RECREATE);
			graph_service = new GraphService(graph);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		try
		{
			SimpleAttribute[] obj_att = new SimpleAttribute[]
			{
				new SimpleAttribute("object", "ok")
			};

			obj_factory = new ObjectFactory(graph_service).Attributes(obj_att);

			SimpleAttribute[] link_att = new SimpleAttribute[]
			{
				new SimpleAttribute("type", "contain")
			};

			link_factory = new LinkFactory(graph_service).Attributes(link_att);
		}
		catch (ExceptionModelFail e)
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

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestFactoryAttributes()
	{
		ObjectList obj = null;
		OctoLink link = null;

		SimpleAttribute[] attr1 = { new SimpleAttribute("test1", 0) };
		SimpleAttribute[] attr2 = { new SimpleAttribute("test2", 0) };
		SimpleAttribute[] attr3 = { new SimpleAttribute("test3", 0) };

		ObjectFactory f1 = new ObjectFactory(graph_service).Attributes(attr1).Attributes(attr2, attr3);
		obj = f1.Create(1);

		LinkFactory f2 = new LinkFactory(graph_service).Attributes(attr2, attr3).Attributes(attr1);
		link = f2.Attributes(new SimpleAttribute("type", "1")).OneToOne(obj.get(0), obj.get(0));

		assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test1"), true);
		assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test2"), true);
		assertEquals("misisng obj attribute", obj.get(0).TestAttribute("test3"), true);

		assertEquals("misisng link attribute", link.TestAttribute("test1"), true);
		assertEquals("misisng link attribute", link.TestAttribute("test2"), true);
		assertEquals("misisng link attribute", link.TestAttribute("test3"), true);
	}

/**
 * check that object factory creates required amount of objects
 * with given property
 * */
	@Test
	public void TestObjectsCreate()
	{
		ObjectList obj = null;

		obj = obj_factory.Create(N);
		assertEquals("created more objects", obj.size(), N);

		for(int i = 0; i < N; i++)
		{
			assertEquals("created something wrong"
				, obj.get(i).GetAttribute("object").GetValue(), "ok");
		}
	}

/**
 * check that link factory create correct link
 * */
	@Test
	public void TestLinkCreate()
	{
		ObjectList obj = null;

		OctoLink link = null;

		obj = obj_factory.Create(2);

		link = link_factory.OneToOne(obj.get(0), obj.get(1));

		assertEquals("created something wrong"
			, link.GetAttribute("type").GetString(), "contain");
	}

	/**
	 * check that {@link LinkFactory#OneToOne} creates correct link
	 * */
	@Test
	public void TestConnectorOneToOne()
	{
		ObjectList obj = obj_factory.Create(2);

		link_factory.OneToOne(obj.get(0), obj.get(1));

		assertEquals("link does not exist"
			, obj.get(0).GetOutLinks().Filter("type", "contain").size(), 1);
		assertEquals("link does not exist"
			, obj.get(1).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#OneToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorOneToAll()
	{
		OctoObject obj = obj_factory.Create();
		ObjectList objs = obj_factory.Create(N);

		link_factory.OneToEvery(obj, objs);

		assertEquals("link does not exist"
			, obj.GetOutLinks().Filter("type", "contain").size(), N);

		for(int i = 0; i < N; i++)
			assertEquals("link does not exist"
				, objs.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#EveryToOne} creates correct link
	 * */
	@Test
	public void TestConnectorAllToOne()
	{
		OctoObject obj = obj_factory.Create();
		ObjectList objs = obj_factory.Create(N);

		link_factory.EveryToOne(objs, obj);

		assertEquals("link does not exist"
			, obj.GetInLinks().Filter("type", "contain").size(), N);

		for(int i = 0; i < N; i++)
			assertEquals("link does not exist"
				, objs.get(i).GetOutLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#EveryToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToEvery()
	{
		ObjectList objs1 = obj_factory.Create(N);
		ObjectList objs2 = obj_factory.Create(N);

		link_factory.EveryToEvery(objs1, objs2);

		for(int i = 0; i < N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link LinkFactory#AllToAll} creates correct link
	 * */
	@Test
	public void TestConnectorAllToAll()
	{
		ObjectList objs1 = obj_factory.Create(N);
		ObjectList objs2 = obj_factory.Create(N);

		link_factory.AllToAll(objs1, objs2);

		for(int i = 0; i < N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), N);

			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), N);
		}
	}

	/**
	 * check that {@link LinkFactory#EveryToChunks} creates correct link
	 * */
	@Test
	public void TestConnectorAllToChunks()
	{
		ObjectList objs1 = obj_factory.Create(N);
		ObjectList objs2 = obj_factory.Create(2*N*N);

		link_factory.EveryToChunks(objs1, objs2);

		for(int i = 0; i < N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2*N);
		}

		for(int i = 0; i < 2*N*N; i++)
		{
			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery()
	{
		ObjectList objs1 = obj_factory.Create(2*N*N);
		ObjectList objs2 = obj_factory.Create(N);

		link_factory.ChunksToEvery(objs1, objs2);

		for(int i = 0; i < 2*N*N; i++)
			assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < N; i++)
			assertEquals("in link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2*N);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_LastLess()
	{
		int K = 6;

		ObjectList objs1 = obj_factory.Create(2*N*N-K);
		ObjectList objs2 = obj_factory.Create(N);

		link_factory.ChunksToEvery_LastLess(objs1, objs2);

		for(int i = 0; i < 2*N*N-K; i++)
			assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < N-1; i++)
			assertEquals("in link does not exist"
					, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2*N);

		assertEquals("in link does not exist"
				, objs2.get(N-1).GetInLinks().Filter("type", "contain").size(), 2*N-K);
	}
	/**
	 * check that {@link LinkFactory#EveryToChunks_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_LastLess()
	{
		int K = 6;

		ObjectList objs1 = obj_factory.Create(N);
		ObjectList objs2 = obj_factory.Create(2*N*N - K);

		link_factory.EveryToChunks_LastLess(objs1, objs2);

		for(int i = 0; i < N-1; i++)
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2*N);

		assertEquals("link does not exist"
			, objs1.get(N-1).GetOutLinks().Filter("type", "contain").size(), 2*N - K);

		for(int i = 0; i < 2*N*N - K; i++)
			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ObjectList objs_from1 = obj_factory.Create(len);
		ObjectList objs_from2 = obj_factory.Create(len);
		ObjectList objs_from3 = obj_factory.Create(len);

		ObjectList objs_to1 = obj_factory.Create(sum);
		ObjectList objs_to2 = obj_factory.Create(sum+1);
		ObjectList objs_to3 = obj_factory.Create(sum-1);

		link_factory.EveryToChunks_Guided(objs_from1, objs_to1, arr);
		link_factory.EveryToChunks_Guided(objs_from2, objs_to2, arr);
		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			link_factory.EveryToChunks_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objs_from1.get(i).GetOutLinks().Filter("type", "contain").size(), arr[i]);
	}

	/**
	 * check that {@link LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ObjectList objs_from1 = obj_factory.Create(sum);
		ObjectList objs_from2 = obj_factory.Create(sum+1);
		ObjectList objs_from3 = obj_factory.Create(sum-1);

		ObjectList objs_to1 = obj_factory.Create(len);
		ObjectList objs_to2 = obj_factory.Create(len);
		ObjectList objs_to3 = obj_factory.Create(len);

		link_factory.ChunksToEvery_Guided(objs_from1, objs_to1, arr);
		link_factory.ChunksToEvery_Guided(objs_from2, objs_to2, arr);

		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			link_factory.ChunksToEvery_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objs_to1.get(i).GetInLinks().Filter("type", "contain").size(), arr[i]);
	}
}
