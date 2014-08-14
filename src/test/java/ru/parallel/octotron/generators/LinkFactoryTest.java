package ru.parallel.octotron.generators;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.parallel.octotron.core.graph.impl.GraphService;
import ru.parallel.octotron.core.model.ModelLink;
import ru.parallel.octotron.core.model.ModelObject;
import ru.parallel.octotron.core.model.impl.ModelObjectList;
import ru.parallel.octotron.core.primitive.SimpleAttribute;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.neo4j.impl.Neo4jGraph;

import static org.junit.Assert.assertEquals;

public class LinkFactoryTest
{
	private static Neo4jGraph graph;
	private static GraphService graph_service;

	private static ObjectFactory obj_factory;
	private static LinkFactory link_factory;

	private static final int N = 10; // some testing param

	@BeforeClass
	public static void Init() throws Exception
	{
		LinkFactoryTest.graph = new Neo4jGraph( "dbs/"
			+ LinkFactoryTest.class.getSimpleName(), Neo4jGraph.Op.RECREATE);
		GraphService.Init (graph);

		LinkFactoryTest.obj_factory = new ObjectFactory()
			.Constants(new SimpleAttribute("object", "ok"));

		LinkFactoryTest.link_factory = new LinkFactory()
			.Constants(new SimpleAttribute("type", "contain"));
	}

	@AfterClass
	public static void Delete() throws Exception
	{
		LinkFactoryTest.graph.Shutdown();
		LinkFactoryTest.graph.Delete();
	}

/**
 * check that link factory create correct link
 * */
	@Test
	public void TestLinkCreate()
	{
		ModelObjectList obj = LinkFactoryTest.obj_factory.Create(2);

		ModelLink link = LinkFactoryTest.link_factory.OneToOne(obj.get(0), obj.get(1));

		assertEquals("created something wrong"
			, link.GetAttribute("type").GetString(), "contain");
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#OneToOne} creates correct link
	 * */
	@Test
	public void TestConnectorOneToOne()
	{
		ModelObjectList obj = LinkFactoryTest.obj_factory.Create(2);

		LinkFactoryTest.link_factory.OneToOne(obj.get(0), obj.get(1));

		assertEquals("link does not exist"
			, obj.get(0).GetOutLinks().Filter("type", "contain").size(), 1);
		assertEquals("link does not exist"
			, obj.get(1).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#OneToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorOneToAll()
	{
		ModelObject obj = LinkFactoryTest.obj_factory.Create();
		ModelObjectList objs = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.OneToEvery(obj, objs);

		assertEquals("link does not exist"
			, obj.GetOutLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("link does not exist"
				, objs.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToOne} creates correct link
	 * */
	@Test
	public void TestConnectorAllToOne()
	{
		ModelObject obj = LinkFactoryTest.obj_factory.Create();
		ModelObjectList objs = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToOne(objs, obj);

		assertEquals("link does not exist"
			, obj.GetInLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("link does not exist"
				, objs.get(i).GetOutLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToEvery()
	{
		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToEvery(objs1, objs2);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#AllToAll} creates correct link
	 * */
	@Test
	public void TestConnectorAllToAll()
	{
		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.AllToAll(objs1, objs2);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), LinkFactoryTest.N);

			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), LinkFactoryTest.N);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToChunks} creates correct link
	 * */
	@Test
	public void TestConnectorAllToChunks()
	{
		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N);

		LinkFactoryTest.link_factory.EveryToChunks(objs1, objs2);

		for(int i = 0; i < LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);
		}

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N; i++)
		{
			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
		}
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery()
	{
		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.ChunksToEvery(objs1, objs2);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N; i++)
			assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < LinkFactoryTest.N; i++)
			assertEquals("in link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_LastLess()
	{
		int K = 6;

		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N - K);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);

		LinkFactoryTest.link_factory.ChunksToEvery_LastLess(objs1, objs2);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N -K; i++)
			assertEquals("out link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 1);

		for(int i = 0; i < LinkFactoryTest.N -1; i++)
			assertEquals("in link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);

		assertEquals("in link does not exist"
			, objs2.get(LinkFactoryTest.N - 1).GetInLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N - K);
	}
	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#EveryToChunks_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_LastLess()
	{
		int K = 6;

		ModelObjectList objs1 = LinkFactoryTest.obj_factory.Create(LinkFactoryTest.N);
		ModelObjectList objs2 = LinkFactoryTest.obj_factory.Create(2 * LinkFactoryTest.N * LinkFactoryTest.N - K);

		LinkFactoryTest.link_factory.EveryToChunks_LastLess(objs1, objs2);

		for(int i = 0; i < LinkFactoryTest.N -1; i++)
			assertEquals("link does not exist"
				, objs1.get(i).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N);

		assertEquals("link does not exist"
			, objs1.get(LinkFactoryTest.N - 1).GetOutLinks().Filter("type", "contain").size(), 2 * LinkFactoryTest.N - K);

		for(int i = 0; i < 2* LinkFactoryTest.N * LinkFactoryTest.N - K; i++)
			assertEquals("link does not exist"
				, objs2.get(i).GetInLinks().Filter("type", "contain").size(), 1);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorEveryToChunks_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ModelObjectList objs_from1 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objs_from2 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objs_from3 = LinkFactoryTest.obj_factory.Create(len);

		ModelObjectList objs_to1 = LinkFactoryTest.obj_factory.Create(sum);
		ModelObjectList objs_to2 = LinkFactoryTest.obj_factory.Create(sum + 1);
		ModelObjectList objs_to3 = LinkFactoryTest.obj_factory.Create(sum - 1);

		LinkFactoryTest.link_factory.EveryToChunks_Guided(objs_from1, objs_to1, arr);
		LinkFactoryTest.link_factory.EveryToChunks_Guided(objs_from2, objs_to2, arr);
		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			LinkFactoryTest.link_factory.EveryToChunks_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objs_from1.get(i).GetOutLinks().Filter("type", "contain").size(), arr[i]);
	}

	/**
	 * check that {@link ru.parallel.octotron.generators.LinkFactory#ChunksToEvery_LastLess} creates correct link
	 * */
	@Test
	public void TestConnectorChunksToEvery_Guided()
	{
		int[] arr = {1,2,3,4,5,6};

		int sum = 0;
		int len = arr.length;

		for(int i : arr)
			sum += i;

		ModelObjectList objs_from1 = LinkFactoryTest.obj_factory.Create(sum);
		ModelObjectList objs_from2 = LinkFactoryTest.obj_factory.Create(sum + 1);
		ModelObjectList objs_from3 = LinkFactoryTest.obj_factory.Create(sum - 1);

		ModelObjectList objs_to1 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objs_to2 = LinkFactoryTest.obj_factory.Create(len);
		ModelObjectList objs_to3 = LinkFactoryTest.obj_factory.Create(len);

		LinkFactoryTest.link_factory.ChunksToEvery_Guided(objs_from1, objs_to1, arr);
		LinkFactoryTest.link_factory.ChunksToEvery_Guided(objs_from2, objs_to2, arr);

		System.err.println("^^ above warning is ok ^^");

		boolean detected = false;
		try
		{
			LinkFactoryTest.link_factory.ChunksToEvery_Guided(objs_from3, objs_to3, arr);
		}
		catch(ExceptionModelFail e) { detected = true; }

		assertEquals("wrong size did not throw exception", detected, true);

		for(int i = 0; i < len; i++)
			assertEquals("out link does not exist"
				, objs_to1.get(i).GetInLinks().Filter("type", "contain").size(), arr[i]);
	}
}
